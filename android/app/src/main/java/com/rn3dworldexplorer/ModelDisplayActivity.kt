package com.rn3dworldexplorer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelDisplayActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var closeButton: Button
    private lateinit var sceneView: ArSceneView
    private lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar_activity)

        // Initialize views
        closeButton = findViewById(R.id.closeButton)
        progressBar = findViewById(R.id.progressBar)
        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }
        sceneView.onArSessionFailed = { exception: Exception ->
            Log.e("ARModule", "${exception.message}")
            // If AR is not available, we add the model directly to the scene for a 3D only usage
            sceneView.addChild(modelNode)
        }

        // Set close button listener
        closeButton.setOnClickListener {
            finish()
        }

        // Load model if path is provided
        intent.getStringExtra("MODEL_PATH")?.let { modelPath ->
            loadModel(modelPath)

        } ?: showMessage("Model path is null")
    }

    private fun loadModel(glbFileLocation: String) {
        Log.d("ARModule", "loadModel path: $glbFileLocation")
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = glbFileLocation,
                        scaleToUnits = 1f,
                        centerOrigin = Position(0.0f)
                    )
                    {
                        sceneView.planeRenderer.isVisible = true
                        val materialInstance = it.materialInstances[0]
                    }
                }
                sceneView.addChild(modelNode)
                modelNode.anchor()
                sceneView.planeRenderer.isVisible = false
            } catch (e: Exception) {
                showMessage("Error loading model: ${e.message}")
                Log.e("ARModule", "Error occurred: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}