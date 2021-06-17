package com.navigatorsguide.app.utils

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Questions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagePreviewActivity : BaseActivity() {
    lateinit var previewImageView: ImageView
    lateinit var item: List<Questions>
    var qid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        qid = intent.getIntExtra(AppConstants.QID, 0)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setTitle("Preview");
        previewImageView = findViewById(R.id.preview_image_view)

        launch {
            withContext(Dispatchers.Default) {
                item = AppDatabase.invoke(context = this@ImagePreviewActivity)
                    .getQuestionsDao().getUserResponse(qid)
            }
            setPreviewImage()
        }
    }

    private fun setPreviewImage() {
        if (item.get(0).attachment != null) {
            val bitmap: Bitmap = ImageBitmapUtils.convertStringToBitmap(item.get(0).attachment!!)
            previewImageView.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
}