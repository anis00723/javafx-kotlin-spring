package me.dragan.javafxkotlinspring.controller

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.Pane
import me.dragan.javafxkotlinspring.event.FileAddedEvent
import me.dragan.javafxkotlinspring.event.FileSelectedEvent
import org.springframework.stereotype.Component
import java.io.File
import java.nio.charset.Charset

/**
 * Controller for fileData.fxml - which displays the contents of file in a TextArea
 */
@Component
class FileDataController(eventBus: EventBus) : EventAwareController(eventBus) {

    companion object {
        const val PREVIEW_BYTES = 8192
    }

    // view
    @FXML
    lateinit var fileContentsArea: TextArea

    @FXML
    lateinit var fileContentsLabel: Label


    fun load(): Pane {
        val loader = FXMLLoader(javaClass.getResource("/fileData.fxml"))
        loader.setController(this)
        return loader.load<Pane>()
    }


    @Subscribe
    fun handleFileAdded(e: FileAddedEvent) {
        println("FileDataController processing FileAddedEvent")

        loadAndDisplayContents(e.file)
    }


    @Subscribe
    fun handleFileSelectionChanged(e: FileSelectedEvent) {
        println("FileDataController processing FileSelectedEvent")

        loadAndDisplayContents(e.file)
    }

    /**
     * Either load the whole file or a preview of it.
     */
    private fun loadAndDisplayContents(file: File) {
        if (file.length() < PREVIEW_BYTES) {
            fileContentsArea.text = String(file.readBytes(), Charset.defaultCharset())
            fileContentsLabel.text = "Full File Contents"
        } else {
            // preview first PREVIEW_BYTES only - could be a very large file!
            val stream = file.inputStream()
            val bytes = stream.readNBytes(PREVIEW_BYTES)
            fileContentsArea.text = String(bytes, Charset.defaultCharset())
            fileContentsLabel.text = "File Content Preview - First $PREVIEW_BYTES Bytes Only"
        }
    }
}