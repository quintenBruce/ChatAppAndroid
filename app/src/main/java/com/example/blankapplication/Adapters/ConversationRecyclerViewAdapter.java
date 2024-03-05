package com.example.blankapplication.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blankapplication.R;
import com.example.blankapplication.data.models.Conversation;

import java.util.List;

    /**
 * <a href="https://developer.android.com/develop/ui/views/layout/recyclerview">Android Studio Documentation</a>
 * <p>Once you determine your layout, you need to implement your <b>Adapter</b> and <b>ViewHolder</b>. These two classes work together to define how your data is displayed. </p>
 * <ul>
 * <li>The ViewHolder is a wrapper around a View that contains the layout for an individual item in the list. </li>
 * <li>The Adapter creates ViewHolder objects as needed and also sets the data for those views.</li>
 * </ul>
 * <p>When you define your adapter, you override three key methods:</p>
 * <li>onCreateViewHolder()</li>
 * <li>onBindViewHolder()</li>
 * <li>getItemCount()</li>
 */



/*
 * Custom RecyclerView Adapter used to bind data to a RecyclerView.
 */
public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    // Data to be displayed in the RecyclerView
    private List<Conversation> conversations;

    // LayoutInflater for inflating the item layout from XML
    // A LayoutInflater in Android is a class that takes an XML file as input and builds the corresponding View objects from it.
    private LayoutInflater mInflater;

    // Interface for handling item click events
    private ItemClickListener mClickListener;

    /**
     * Constructor to initialize the adapter with context and data.
     *
     * @param context The context in which the adapter is created.
     * @param data    The data to be displayed in the RecyclerView.
     */
    public ConversationRecyclerViewAdapter(Context context, List<Conversation> data) {
        this.mInflater = LayoutInflater.from(context);
        this.conversations = data;
    }

    /**
     * RecyclerView calls this method whenever it needs to create a new ViewHolder. The method creates and initializes the ViewHolder and its associated View, but does not fill in the view's contents—the ViewHolder has not yet been bound to specific data.
     *
     * @param parent   The parent view group into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance with the inflated view.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate xml file into View object
        View view = mInflater.inflate(R.layout.conversationrecyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called to bind data to the views within the ViewHolder.
     * RecyclerView calls this method to associate a ViewHolder with data. The method fetches the appropriate data and uses the data to fill in the view holder's layout.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);

        holder.conversationName.setText(conversation.getName());
        holder.unreadMessages.setText("7");
        holder.itemView.setTag(conversations.get(position));
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return conversations.size();
    }

    /**
     * ViewHolder class representing the views for a single item in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TextView to display the data for this item
        TextView conversationName;
        TextView unreadMessages;

        /**
         * ViewHolder constructor to initialize views and set up click listener.
         *
         * @param itemView The item view representing a single item in the RecyclerView.
         */
        ViewHolder(View itemView) {
            super(itemView);
            conversationName = itemView.findViewById(R.id.rowConversationName);
            unreadMessages = itemView.findViewById(R.id.unreadMessages);
            itemView.setOnClickListener(this);

        }

        /**
         * Handles item click events.
         *
         * @param view The clicked view.
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Returns the data at a specific position.
     *
     * @param id The position of the item in the data set.
     * @return The data at the specified position.
     */
    public Conversation getItem(int id) {
        return conversations.get(id);
    }

    /**
     * Sets the item click listener.
     *
     * @param itemClickListener The item click listener to set.
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * Interface for handling item click events.
     */
    public interface ItemClickListener {
        /**
         * Called when an item is clicked.
         *
         * @param view     The clicked view.
         * @param position The position of the clicked item.
         */
        void onItemClick(View view, int position);
    }

    public void updateItems(List<Conversation> newConversations) {
       conversations = newConversations;
       this.notifyDataSetChanged();
    }
}

