import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.andriodtv.R

class AppAdapter(
    private val apps: List<App>,
    private val onItemClick: (App) -> Unit
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.app_icon)
        val name: TextView = itemView.findViewById(R.id.app_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]

        holder.icon.setImageDrawable(app.icon) // Set app icon
        holder.name.text = app.name // Set app name

        holder.itemView.setOnClickListener {
            onItemClick(app) // Open app when clicked
        }

        // D-pad Navigation Animation
        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start()
            } else {
                view.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }
    }

    override fun getItemCount(): Int = apps.size
}
