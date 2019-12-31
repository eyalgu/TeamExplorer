package app.eyal.teamexplorer.ui


import android.widget.ImageView
import android.widget.TextView
import app.eyal.teamexplorer.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.preload.Preloadable
import com.bumptech.glide.Glide

@EpoxyModelClass(layout = R.layout.user_row_item)
abstract class UserRowModel : EpoxyModelWithHolder<UserRowViewHolder>() {

    @EpoxyAttribute
    lateinit var imageUrl: String
    @EpoxyAttribute
    lateinit var displayName: String

    override fun bind(viewHolder: UserRowViewHolder) {
        viewHolder.glide.loadImage(imageUrl).into(viewHolder.avatar)
        viewHolder.displayName.text = displayName
    }

    override fun unbind(viewHolder: UserRowViewHolder) {
        viewHolder.glide.clear(viewHolder.avatar)
        viewHolder.avatar.setImageDrawable(null)
    }
}

class UserRowViewHolder : KotlinHolder(), Preloadable {
    val avatar by bind<ImageView>(R.id.avatar)
    val displayName by bind<TextView>(R.id.display_name)
    val glide by lazy { Glide.with(avatar.context) }
    override val viewsToPreload by lazy { listOf(avatar) }
}