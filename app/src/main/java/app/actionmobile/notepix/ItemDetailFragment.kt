package app.actionmobile.notepix

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.actionmobile.notepix.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import android.content.Intent
import android.R.attr.path
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult




/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.toolbar_layout?.title = item?.content
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.item_detail.text = it.details
        }
        rootView.mainImageView.setOnClickListener { view ->

            val intent = Intent()
            intent.action = android.content.Intent.ACTION_VIEW
            val outfile = "data/data/app.actionmobile.notepix/files/seconds.png"
            //val outfile = "data/data/seconds.png"

            Log.d("Main", outfile)
            val file =
                File(outfile)
            val fileUri = Uri.fromFile( file)
            Toast.makeText(rootView.context,fileUri.path, Toast.LENGTH_LONG ).show()
            intent.setDataAndType(fileUri, "image/png");
//            startActivity(intent)



        }
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
