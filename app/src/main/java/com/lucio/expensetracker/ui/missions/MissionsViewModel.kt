package com.lucio.expensetracker.ui.missions

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucio.expensetracker.domain.model.Mission
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MissionsViewModel(
    private val missionRepository: MissionRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    val missions: StateFlow<List<Mission>> = missionRepository.getAllMissions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMission(name: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            missionRepository.insertMission(
                Mission(name = name, startDate = startDate, endDate = endDate)
            )
        }
    }

    fun updateMission(mission: Mission) {
        viewModelScope.launch {
            missionRepository.updateMission(mission)
        }
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch {
            missionRepository.deleteMission(mission)
        }
    }

    fun generatePdfForMission(context: Context, mission: Mission) {
        viewModelScope.launch {
            val expenses = expenseRepository.getExpensesByMissionId(mission.id).first()
            val receiptPaths = expenses.mapNotNull { it.receiptPath }

            if (receiptPaths.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No receipts found for this mission", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            withContext(Dispatchers.IO) {
                try {
                    val pdfDocument = PdfDocument()
                    receiptPaths.forEachIndexed { index, path ->
                        val uri = Uri.parse(path)
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            if (bitmap != null) {
                                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                                val page = pdfDocument.startPage(pageInfo)
                                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                                pdfDocument.finishPage(page)
                                bitmap.recycle()
                            }
                        }
                    }

                    val fileName = "Mission_${mission.name.replace(" ", "_")}_Receipts.pdf"
                    val outputStream: OutputStream?

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                        }
                        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                        outputStream = uri?.let { resolver.openOutputStream(it) }
                    } else {
                        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        if (!documentsDir.exists()) documentsDir.mkdirs()
                        val file = File(documentsDir, fileName)
                        outputStream = FileOutputStream(file)
                    }

                    outputStream?.use {
                        pdfDocument.writeTo(it)
                    }
                    pdfDocument.close()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "PDF saved to Documents folder: $fileName", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
