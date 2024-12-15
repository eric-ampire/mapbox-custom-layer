package com.ericampire.customlayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ericampire.customlayer.ui.theme.MapboxlayerTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.DoubleValue
import com.mapbox.maps.extension.compose.style.layers.generated.HeatmapLayer
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapboxlayerTheme {
                MapboxView()
            }
        }
    }

    @Composable
    private fun MapboxView() {

        val coroutineScope = rememberCoroutineScope()
        val mapState = rememberMapState {
            coroutineScope.launch {
                mapLoadingErrorEvents.onEach {
                    // Error occurred when loading the map, try to handle it gracefully here
                }
            }
            coroutineScope.launch {
                styleLoadedEvents.first().let {
                    // Map is setup and style has loaded, Now you can add data or make other map adjustments.
                }
            }
        }
        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.0)
                center(Point.fromLngLat(-98.0, 39.5))
                pitch(0.0)
                bearing(0.0)
            }
        }

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            content = {
                WorldHeatmapLayer()
            }
        )
    }

    @Composable
    fun WorldHeatmapLayer(modifier: Modifier = Modifier) {
        HeatmapLayer(
            layerId = "heatmap",
            sourceState = rememberGeoJsonSourceState {
                data = GeoJSONData("asset://geojson/heatmap.geojson")
                cluster = BooleanValue(false)
            },
        )
    }

    @Composable
    fun PedestrianNetwork() {
        LineLayer(
            layerId = "pedestrian-network-layer",
            sourceState = rememberGeoJsonSourceState {
                data = GeoJSONData("asset://geojson/pedestrian-network.geojson")
                cluster = BooleanValue(false)
            },
            init = {
                lineColor = ColorValue(Color.Red)
                lineWidth = DoubleValue(5.0)
            }
        )
    }

    @Composable
    private fun CyclingNetworkLayer() {
        LineLayer(
            layerId = "cycling-network-layer",
            sourceState = rememberGeoJsonSourceState {
                data = GeoJSONData("asset://geojson/cycling-network.geojson")
                cluster = BooleanValue(false)
            },
            init = {
                lineColor = ColorValue(Color.Green)
                lineWidth = DoubleValue(3.0)
            }
        )
    }
}