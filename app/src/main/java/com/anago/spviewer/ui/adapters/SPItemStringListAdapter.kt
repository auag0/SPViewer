package com.anago.spviewer.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class SPItemStringListAdapter(
    private val mContext: Context,
    private val mRecyclerView: RecyclerView
) :
    RecyclerView.Adapter<SPItemStringListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val drag: ImageView = itemView.findViewById(R.id.drag)
    }

    private val mItemTouchHelper: ItemTouchHelper =
        ItemTouchHelper(ItemTouchHelperCallback(this)).apply {
            attachToRecyclerView(mRecyclerView)
        }

    private var mStrings = emptyList<String>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitStrings(strings: List<String>) {
        mStrings = strings
        notifyDataSetChanged()
    }

    fun getSet(): Set<String> {
        return mStrings.toSet()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.listitem_string, parent, false)
        )

    override fun getItemCount(): Int = mStrings.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = mStrings[position]
        holder.text.text = string

        holder.itemView.setOnClickListener {
            val editText = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_add_string, null, false) as TextInputEditText
            editText.setText(string)
            editText.hint = string
            MaterialAlertDialogBuilder(mContext).setTitle("Change String").setView(editText)
                .setPositiveButton("Change") { _, _ ->
                    val oldStrings = mStrings.toMutableList()
                    oldStrings[position] = editText.text.toString()
                    mStrings = oldStrings
                    notifyItemChanged(position)
                }.setNeutralButton("Delete") { _, _ ->
                    val oldStrings = mStrings.toMutableList()
                    oldStrings.removeAt(position)
                    mStrings = oldStrings
                    notifyItemRemoved(position)
                }
                .setNegativeButton("Cancel", null).show()
        }

        holder.drag.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mItemTouchHelper.startDrag(holder)
            }
            false
        }
    }

    fun onItemMove(fromPostion: Int, toPosition: Int) {
        val oldStrings = mStrings.toMutableList()
        val movedItem = oldStrings.removeAt(fromPostion)
        oldStrings.add(toPosition, movedItem)
        mStrings = oldStrings
        notifyItemMoved(fromPostion, toPosition)
    }

    private class ItemTouchHelperCallback(private val adapter: SPItemStringListAdapter) :
        ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            adapter.onItemMove(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }
}