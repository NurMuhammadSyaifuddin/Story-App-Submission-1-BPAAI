package com.project.storyapp.utils

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.storyapp.R
import com.project.storyapp.databinding.CustomDialogInfoBinding
import com.project.storyapp.databinding.LayoutBottomPickBinding
import com.project.storyapp.databinding.LayoutLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.*
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun getTimeLineUploaded(context: Context, timeStamp: String): String {
    val currentTime = getCurrentDate()
    val uploadTime = parseUTCDate(timeStamp)
    val diff: Long = currentTime.time - uploadTime.time
    val second = diff / 1000
    val minutes = second / 60
    val hours = minutes / 60
    val days = hours / 24

    val label = when (minutes.toInt()) {
        0 -> "$second ${context.getString(R.string.second_ago)}"
        in 1..59 -> "$minutes ${context.getString(R.string.minutes_ago)}"
        in 60..1440 -> "$hours ${context.getString(R.string.hours_ago)}"
        else -> "$days ${context.getString(R.string.days_ago)}"
    }

    return label
}

fun parseUTCDate(timeStamp: String): Date =
    try {
        val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timeStamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }

fun getCurrentDate(): Date = Date()

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun showAlertLoading(context: Context): AlertDialog {
    val binding = LayoutLoadingBinding.inflate(LayoutInflater.from(context), null, false)

    return MaterialAlertDialogBuilder(context, R.style.CustomDialogLoading)
        .setView(binding.root)
        .setCancelable(false)
        .create()
}

fun hideSoftKeyboard(context: Context, view: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun showBottomSheetDialogImage(
    context: Context,
    listenerPickCamera: () -> Unit,
    listenerPickGallery: () -> Unit
): BottomSheetDialog {
    val binding = LayoutBottomPickBinding.inflate(LayoutInflater.from(context), null, false)

    val dialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
        setCancelable(true)
        dismissWithAnimation = true
    }

    binding.apply {
        btnPickCamera.setOnClickListener {
            listenerPickCamera()
            dialog.dismiss()
        }

        btnPickGallery.setOnClickListener {
            listenerPickGallery()
            dialog.dismiss()
        }
    }

    return dialog
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun isPermissionGranted(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

enum class LocationPicker {
    IsPicked, Latitude, Longitude
}

fun showInfoDialog(context: Context, desc: String, listener: (Dialog) -> Unit) {
    val binding = CustomDialogInfoBinding.inflate(LayoutInflater.from(context), null, false)

    binding.tvMessage.text = desc

    val dialog = Dialog(context, R.style.AlertDialogTheme)
    dialog.apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        setContentView(binding.root)
        show()
    }

    binding.buttonOk.setOnClickListener {
        listener(dialog)
    }
}

val indonesianLocation = LatLng(-2.3932797, 108.8507139)

fun parseAddressLocation(context: Context, lat: Double, lon: Double): String {
    val geocoder = Geocoder(context)
    val geoLocation = geocoder.getFromLocation(lat, lon, 1)

    return if (geoLocation.size > 0) {
        val location = geoLocation[0]
        val fullAddress = location.getAddressLine(0)
        StringBuilder("📌 ").append(fullAddress).toString()
    } else
        "📌 Location Unknown"
}

const val LOCATION_PERMISSION_CODE = 30

private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
val scope = CoroutineScope(dispatcher)

fun bitmapFromURL(context: Context, urlString: String): Bitmap {
    return try {
        /* allow access content from URL internet */
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        /* fetch image data from URL */
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        BitmapFactory.decodeResource(context.resources, R.color.gray)
    }
}
