package com.arts.mapapeli.ui.mapa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.SparseIntArray
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.arts.mapapeli.R
import com.arts.mapapeli.application.MyApp
import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.application.map.DecodePointsDraw
import com.arts.mapapeli.application.map.GoogleApiProvider
import com.arts.mapapeli.data.source.DataSource
import com.arts.mapapeli.databinding.FragmentMapaBinding
import com.arts.mapapeli.domain.remote.RepositoryMapImpl
import com.arts.mapapeli.presentation.MapaViewModel
import com.arts.mapapeli.presentation.MapaViewModelFactory
import com.arts.mapapeli.presentation.ShareViewModel
import com.arts.mapapeli.ui.mapa.MapaDetalleFragment.Companion.newInstance
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*


class MapaFragment : Fragment(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback{

    private var _binding: FragmentMapaBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mIsFirstTime = true
    private var userLocationAccuracyCircle: Circle? = null
    private var circleOptions = CircleOptions()
    private lateinit var mPolylineList: List<LatLng>
    private lateinit var mPolylineOptions: PolylineOptions
    private var mMarker:Marker? = null
    private var address:String = ""
    private lateinit var currenUsertLatLng: LatLng
    private lateinit var clickMarkerUsuario: LatLng
    private lateinit var mlocation: Location

    private val mapaViewModel by viewModels<MapaViewModel> {MapaViewModelFactory(
        RepositoryMapImpl(DataSource())
    )}
    private val shareViewModel: ShareViewModel by activityViewModels() //para interacmbiar datos, despues de creado se asiga el mismo
    //private val shareViewModel: ShareViewModel by viewModels() //perdilo al ambito del fragment, si otro fragment lo pide, se creara otro ambito

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.i("primero", "primero onCreateView")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("primero", "primero onViewCreated")
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_mapHome) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        //Esta propiedad sirve para iniciar o detener la ubicacion del usuario cada vez que sea conveniente.
        //Pero primero se debe obtener los permisos de ubicacion por parte del usuario
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    override fun onResume() {
        super.onResume()
        Log.i("primero", "primero onResume")
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            if(getRotation(requireContext()).equals("vertical")){ //es vertical o portrait.
                Log.i("orientacion", "vertical")
            }else if(getRotation(requireContext()).equals("vertical_inversa")){
                Log.i("orientacion", "vertical_inversa")
            }else if(getRotation(requireContext()).equals("horizontal")){
                Log.i("orientacion", "horizontal")
                //shareViewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
                //shareViewModel.updateData("Maggiver Acevedo")

                if(shareViewModel.dataShareLiveData != null){
                    Log.i("livedata", "infor")
                    shareViewModel.dataShareLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        Log.i("livedata", "infor ${it.routes}")
                    })
                }
                shareViewModel.getData().observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                    Log.i("orientacion", "$it")
                })
            }else{
                Log.i("orientacion", "horizontal_inversa")
                //shareViewModel.updateData("Daniel Acevedo")
                shareViewModel.dataShareLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                    Log.i("orientacion", "$it")
                })
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Este metodo fue implementado por la interfaz OnMapReadyCallback
    //Establece una instancia del mapa de google asociado con MapFragment o MapView y el metodo onMapReady
    //Se activa cuando el mapa esta listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("primero", "primero onMapReady")
        map = googleMap ?: return
        if(!::map.isInitialized){
            return
        }else{
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            map.setOnMyLocationButtonClickListener(this)
            map.setOnMyLocationClickListener(this)

            /*Controls UI*/
            map.uiSettings.isZoomControlsEnabled = true //para mostrar los botones de zoom (+) (-) en el mapa
            map.uiSettings.isCompassEnabled = false //habilitar/deshabilitar brujula
            map.uiSettings.isMyLocationButtonEnabled = true  //se complementa con: mMap.isMyLocationEnabled=true
            map.uiSettings.isIndoorLevelPickerEnabled = false //selector de piso cuando el usuario ve un mapa de interiores
            /*Toolbar map*/
            map.uiSettings.isMapToolbarEnabled = false //aparece en la parte inferior derecha del mapa cuando el usuario presiona un marcador
            /*Gesture map*/
            map.uiSettings.isRotateGesturesEnabled = true //gestos de rotacion
            map.uiSettings.isZoomGesturesEnabled = true //gestos de zoom
            map.uiSettings.isScrollGesturesEnabled = true //para desplazarse por el mapa

            locationRequest = LocationRequest()
            locationRequest.interval = 1000 //tiempo en que se va a estar actualizando la ubicacion del usuario en el mapa
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //establecer la prioridad que va a tener el GPS, para estar trabajando en la actualizacion de la ubicacion y se debe establecer la priodidad
            locationRequest.smallestDisplacement = 5F //establece el desplazamiento minimo entre actualizaciones de ubicacion en metros
            startLocation()
            val options = PolylineOptions()
            options.color(Color.RED)
            options.width(5f)
        }
    }
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "Centrar mi ubicación", Toast.LENGTH_SHORT).show()
        return false
    }
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(
            requireContext(),
            "Mi ubicación ${p0.latitude}, ${p0.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }

    //CallBacK que va estar escuchando cada vez que el usuario se mueva
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult){
            super.onLocationResult(locationResult)
            for (location in locationResult.locations){
                //el objeto location Callback suele ejecutar trabajos fuera del hilo principal de la aplicacion por eso es bueno hacer esa validacion ya que puede causar un fallo en cualquier momento
                if(context?.applicationContext != null){
                    if(mIsFirstTime){
                        mIsFirstTime = false
                        mlocation = location
                        //OBTENER LA LOCALIZACION DEL USUARIO EN TIEIMPO REAL
                        currenUsertLatLng  = LatLng(mlocation.latitude, mlocation.longitude)
                        showMarkerCustom(mlocation, currenUsertLatLng)
                        map.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(LatLng(location.latitude, location.longitude))
                                    .zoom(15F)
                                    .build()
                            )
                        )

                        //MOSTRAR MARKER, CUANDO SE DA CLICK SOBRE EL MAPA Y MOVER EL MAPA A ESA DIRECCION
                        //SELECCIONADA
                        map.setOnMapClickListener{
                            //Clears the previously touched position
                            //map.clear()
                            showMarkerCustom(location, currenUsertLatLng)
                            val snippet = String.format(
                                Locale.getDefault(),
                                "Lat: %1$.5f, Long: %2$.5f",
                                it.latitude,
                                it.longitude
                            )
                            // Animating to the touched position
                            map.animateCamera(CameraUpdateFactory.newLatLng(currenUsertLatLng))
                            clickMarkerUsuario = LatLng(it.latitude, it.longitude)
                            map.addMarker(
                                MarkerOptions()
                                    .position(clickMarkerUsuario)
                                    .draggable(true)
                                    .title("Ruta")
                                    .snippet(snippet)
                            ).showInfoWindow()
                            map.animateCamera(CameraUpdateFactory.newLatLng(clickMarkerUsuario))
                            Log.i("mapa", "calckBack currenUsertLatLng = $currenUsertLatLng")
                            Log.i("mapa", "calckBack clickMarkerUsuario = $clickMarkerUsuario")
                            Log.i("mapa", "calckBack address = $address")
                            drawRoute(currenUsertLatLng, clickMarkerUsuario, address)
                        }

                        //MOSTRAR MARKER, CON INFORMACION CUANDO SE DA CLICK SOBRE CUALQUIER PUNTO DE INTERES
                        map.setOnPoiClickListener{
                            map.clear()
                            showMarkerCustom(mlocation, currenUsertLatLng)
                            val poiMarker = map.addMarker(
                                MarkerOptions().position(it.latLng).title(
                                    it.name
                                )
                            )
                            poiMarker.showInfoWindow()
                        }
                    }
                }
            }
        }
    }
    private fun showMarkerCustom(location: Location, mCurrentLatLng: LatLng) {
        //preguntamos si ya se establecio un marcador en el mapa
        if(mMarker != null){
            mMarker!!.remove()
            map.clear()
        }
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address> =
            geocoder.getFromLocation(mCurrentLatLng.latitude, mCurrentLatLng.longitude, 1)
        address = addresses.get(0).getAddressLine(0)
        Log.i("mapa", "address = $address")

        mMarker = map.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .rotation(location.bearing)
                .draggable(true)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_32_rec))
                .title("Mi direccion: $address")
        )
        mMarker!!.showInfoWindow()

        circleOptions.center(mCurrentLatLng)
        circleOptions.strokeWidth(4f)
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(32, 255, 0, 0))
        circleOptions.radius(location.accuracy.toDouble())
        userLocationAccuracyCircle = map.addCircle(circleOptions)
        userLocationAccuracyCircle!!.setCenter(mCurrentLatLng)
        userLocationAccuracyCircle!!.setRadius(location.accuracy.toDouble())

        //drawRoute(location, mCurrentLatLng)
    }
    private fun drawRoute(mCurrentLatLng: LatLng, clickMarkerUsuario: LatLng, address: String) {
        Log.i("mapa", "mCurrentLatLng -> lat=${mCurrentLatLng.latitude} long=${mCurrentLatLng.longitude}")
        Log.i("mapa", "clickMarkerUsuario-> lat=${clickMarkerUsuario.latitude} long=lat=${clickMarkerUsuario.longitude}")
        var origin: String = (mCurrentLatLng.latitude).toString()+","+(mCurrentLatLng.longitude).toString()
        var destino: String = (clickMarkerUsuario.latitude).toString()+","+(clickMarkerUsuario.longitude).toString()

        mapaViewModel.directionDriverMap(
            "driving",
            "less_driving",
            origin,
            destino,
            MyApp.context.resources.getString(R.string.google_maps_key)
        ).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("rotacion", "rotacion del mapa")
            when(it){
                    is Resource.Loading ->{
                        Log.i("mapa", "Cargando mapa")
                    }
                    is Resource.Success ->{
                        Log.i("mapa", "Success direction = ${it.data.data}")

                        val route = it.data.data.routes[0]
                        val polylines = route.overviewPolyline
                        val points = polylines.points

                        mPolylineList = DecodePointsDraw.decodePoly(points)
                        mPolylineOptions = PolylineOptions()
                        mPolylineOptions.color(Color.DKGRAY)
                        mPolylineOptions.width(13f)
                        mPolylineOptions.startCap(SquareCap())
                        mPolylineOptions.jointType(JointType.ROUND)
                        mPolylineOptions.addAll(mPolylineList)
                        map.addPolyline(mPolylineOptions)

                        val legs = route.legs[0]
                        val distance = legs.distance.text
                        val duration = legs.duration.text
                        val direccionFinal = legs.endAddress
                        //Toast.makeText(requireContext(), "Direccion final $direccionFinal", Toast.LENGTH_SHORT).show()

                        val distanceAndKm = distance.split(" ".toRegex()).toTypedArray()
                        val distanceValue = distanceAndKm[0].toDouble() //aqui obtienes solo los km
                        val kmText = distanceAndKm[1] //aqui tienes el el texto en km
                        val durationAndMin = duration.split(" ".toRegex()).toTypedArray()
                        var durationValue: Double = durationAndMin[0].toDouble() //aqui obtienes solo los km

                        Log.i("mapa", "distanceValue = $distanceValue")
                        Log.i("mapa", "durationValue = $durationValue")

                        binding.btnIniciarMap.visibility = View.VISIBLE
                        binding.btnIniciarMap.setOnClickListener {
                            var bundle = bundleOf("tiempo" to duration, "distancia" to distance, "dirInicial" to address, "dirDestino" to direccionFinal)
                            val ska = MapaFragmentDirections.actionNavigationMapaToMapaDetalleFragment(bundle)
                            //findNavController().navigate(R.id.action_navigation_mapa_to_mapaDetalleFragment)
                            Navigation.findNavController(it).navigate(ska)
                        }
                    }
                    is Resource.Failure ->{
                        Log.i("mapa", "Failure ${it.exception}")
                    }
                    else ->{
                        Log.i("mapa", "Error no documentado")
                    }
                }
            })
    }

    //Metodo para iniciar el escuchador del la ubicacion del dispositivo
    private fun startLocation(){
        Log.i("primero", "primero startLocation")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED){
                if(gpsActivated()){
                    //cuando este evento se ejecuta va entrar a mLocationCallback y va obtener la localizacion del usuario en tiempo real
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                    ) //La clase Lopper basicamente se utiliza para decir que vamos a mandar varios mensajes al hilo de ejecucion, por eso utilizamos el Runnable para ejecutar ciclicamente la actualizacion del GPS: https://www.it-swarm.dev/es/android/cual-es-el-proposito-de-looper-y-como-usarlo/939681342/

                    map.isMyLocationEnabled = true //se establece mostrar el punto azul de ubicacion despues de tener los permisos por el usuario
                    fusedLocationProviderClient.lastLocation

                }else{
                    showAlertDialogNoActiveGPS()
                }
            }else{
                //si los permisos no estan concedidos por el usuario
                Log.i("mapa", "paso 2: startLocation(): condicion else")
                checkLocationPermissions()
            }
        }else{
            if(gpsActivated()){
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                map.isMyLocationEnabled = false
            }else{
                showAlertDialogNoActiveGPS()
            }
        }
    }

    //Metodo para saber si el usuario tiene el GPS activado
    private fun gpsActivated(): Boolean {
        var isActive: Boolean = false
        var locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //preguntamos si tiene el GPS activado
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive = true
        }
        return isActive
    }

    //Metodo para ir a las configraciones activar el GPS, en caso de que el usuario no tenga activo el GPS
    private fun showAlertDialogNoActiveGPS(){
        AlertDialog.Builder(requireContext())
            .setTitle("ACTIVAR GPS")
            .setMessage("Por favor activa tu GPS para continuar")
            .setCancelable(false)
            .setPositiveButton("IR CONFIGURACIONES") { _, _ ->
                //habilitar los permisos para acceder a la ubicacion del celular
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    SETTINGS_GPS_REQUEST_CODE
                )
            }.show()
    }

    //Metodo lanzado por showAlertDialogNoActiveGPS()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //preguntamos si el usuario quiere activar el GPS
        if(requestCode == SETTINGS_GPS_REQUEST_CODE && gpsActivated()){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            map.isMyLocationEnabled = true
        }else if(requestCode == MY_PERMISSIONS_LOCATION_REQUEST_CODE && !gpsActivated()){
            //en caso contrario que el usuario no haya activado el GPS
            showAlertDialogNoActiveGPS()
        }
    }

    //METODO PARA COMPROBAR SI EL USUARIO CONCEDIO EL PERMISO DE LOCALIZACION Y SI LO CONCEDIO, SI NO SE LO SOLICITA
    fun checkLocationPermissions(){
        //preguntamos si el usuario no concedio los permisos
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            //ActivityCompat.requestPermissions : SOLICITAR QUE EL USUARIO DE PERMISO PARA USAR PERMISOS PELIGROSOS
            Log.i(
                "mapa", "paso 3: checkSelfPermission = ${
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }"
            )

            if(ActivityCompat.shouldShowRequestPermissionRationale( //si es true, se sigue mostrando
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )){
                Log.i("mapa", "paso 4: shouldShowRequestPermissionRationale")
                AlertDialog.Builder(requireContext())
                    .setTitle("PERMISO DE UBICACION")
                    .setMessage("AIPPago requiere acceso a su ubicacion para utilizarse mejor")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar") { _, _ ->
                        //HABILITAR LOS PERMISOS PARA UTILIZAR LA UBICACION DEL CELULAR
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_LOCATION_REQUEST_CODE
                        )
                    }.create().show()
            }else{
                //HABILITAR LOS PERMISOS PARA UTILIZAR LA UBICACION DEL CELULAR
                /*Log.i(
                    "mapa", "paso 23w ${
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }"
                )*/
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_LOCATION_REQUEST_CODE
                )
            }
        }
    }

    //METODO LANZADADO POR checkLocationPermissions()
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == MY_PERMISSIONS_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // == 0
                //preguntamos si el usuario concedio los permisos
                Log.i(
                    "mapa",
                    "paso 42m: permisions[] mayor a cero: grantResults[0] = ${grantResults[0]}"
                )
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if(gpsActivated()){
                        // Permission is granted. Continue the action or workflow in your app.
                        // https://www.udemy.com/course/crea-una-app-como-uber-utilizando-android-studio-y-firebase/learn/lecture/18539638#questions/13014822
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                    }else{
                        showAlertDialogNoActiveGPS()
                    }
                } else {
                    Log.i("mapa", "paso 6:")
                    checkLocationPermissions()
                }
            }else if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
                Log.i(
                    "mapa",
                    "paso 25305: valor dialog= ${
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }"
                )
                if(!ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )){
                    Log.i(
                        "mapa",
                        "paso 25305: permisions[] denied 5555: grantResults[0] = ${grantResults[0]}"
                    )
                    AlertDialog.Builder(requireContext())
                        .setTitle("PERMISO DE UBICACION DENEGADO")
                        .setMessage(
                            "Por favor, conceda el permiso de acceso a su ubicacion, para que la aplicacion " +
                                    "funcione correctamente"
                        )
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, which ->
                            val i = Intent()
                            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            i.addCategory(Intent.CATEGORY_DEFAULT)
                            i.data = Uri.parse("package:" + requireActivity().packageName)
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            //comeFromDetailsAppActivity = true
                            startActivity(i)
                        }.create().show()
                }else{
                    // permission denied, boo! Disable the, functionality that depends on this permission.
                    // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
                    //Explain to the user that the feature is unavailable because  the features requires a permission that the user has denied.
                    checkLocationPermissions()
                    Log.i(
                        "mapa",
                        "paso 25305: dialog= else *******"
                    )
                }
            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_LOCATION_REQUEST_CODE = 100
        private const val SETTINGS_GPS_REQUEST_CODE = 101

        val orientacion: SparseIntArray = SparseIntArray().apply {
            append(Surface.ROTATION_0, 0)
            append(Surface.ROTATION_90, 90)
            append(Surface.ROTATION_180, 180)
            append(Surface.ROTATION_270, 270)
        }

        fun getRotation(context: Context): String? {
            val mWindowManager = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            val mDisplay = mWindowManager.defaultDisplay  //Display mDisplay = mWindowManager.getDefaultDisplay(); //OrientationEventListener
            val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation
            return when (rotation) {
                Surface.ROTATION_0 -> "vertical"
                Surface.ROTATION_90 -> "horizontal"
                Surface.ROTATION_180 -> "vertical_inversa"
                else -> "horizontal inversa"
            }
        }

    }
}