package com.example.blankapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blankapplication.R;
import com.example.blankapplication.data.models.MessageRecycler;
import com.example.blankapplication.data.repositories.MessageRepository;
import com.example.blankapplication.data.repositories.UsersRepository;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {
    // Data to be displayed in the RecyclerView
    private List<MessageRecycler> messages;

    // LayoutInflater for inflating the item layout from XML
    // A LayoutInflater in Android is a class that takes an XML file as input and builds the corresponding View objects from it.
    private LayoutInflater mInflater;
    private UsersRepository usersRepository;
    private MessageRepository messageRepository;


    // Interface for handling item click events
    private MessageRecyclerViewAdapter.ItemClickListener mClickListener;

    /**
     * Constructor to initialize the adapter with context and data.
     *
     * @param context The context in which the adapter is created.
     * @param data    The data to be displayed in the RecyclerView.
     */
    public MessageRecyclerViewAdapter(Context context, List<MessageRecycler> data) {
        this.mInflater = LayoutInflater.from(context);
        this.messages = data;
        this.usersRepository = new UsersRepository();
        this.messageRepository = new MessageRepository();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        MessageRecycler m = messages.get(position);
        if (m.isSender())
            return 1;
        return 0;
    }

    /**
     * RecyclerView calls this method whenever it needs to create a new ViewHolder. The method creates and initializes the ViewHolder and its associated View, but does not fill in the view's contents—the ViewHolder has not yet been bound to specific data.
     *
     * @param parent   The parent view group into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance with the inflated view.
     */
    @Override
    public MessageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate xml file into View object

        if (viewType == 0) {
            View view = mInflater.inflate(R.layout.messagerecyclerview_row, parent, false);
            return new MessageRecyclerViewAdapter.ViewHolder(view, this);
        }
        View view = mInflater.inflate(R.layout.messagerecyclerview_row_sender, parent, false);
        return new MessageRecyclerViewAdapter.ViewHolder(view, this);
    }

    /**
     * Called to bind data to the views within the ViewHolder.
     * RecyclerView calls this method to associate a ViewHolder with data. The method fetches the appropriate data and uses the data to fill in the view holder's layout.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(MessageRecyclerViewAdapter.ViewHolder holder, int position) {
        MessageRecycler message = messages.get(position);
        holder.itemView.setTag(messages.get(position));
        holder.dateTime.setText(message.getFormattedDate());
        holder.dateTime.setVisibility(View.INVISIBLE);
        holder.setDateTimeVisibility();
        holder.sender.setText(message.getSenderUserName());
        holder.content.setText(message.getContent(), TextView.BufferType.SPANNABLE);

        //add OnPreDraw Listener to align content TextView text after size calculations, but before drawing
        holder.content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.content.getViewTreeObserver().removeOnPreDrawListener(this);
                alignText(holder.content, position);
                return true;
            }
        });
    }

    /**
     * Returns the total number of items in the RecyclerView.
     *
     * @return The total number of items.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * ViewHolder class representing the views for a single item in the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TextView to display the data for this item
        TextView content;
        TextView sender;
        TextView dateTime;
        private MessageRecyclerViewAdapter adapter;

        /**
         * ViewHolder constructor to initialize views and set up click listener.
         *
         * @param itemView The item view representing a single item in the RecyclerView.
         */
        ViewHolder(View itemView, MessageRecyclerViewAdapter _adapter) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            sender = itemView.findViewById(R.id.sender);
            dateTime = itemView.findViewById(R.id.dateTime);
            adapter = _adapter;


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MessageRecycler m = (MessageRecycler) itemView.getTag();

                    if (m.isSender()) {
                        showPopupMenu(view);
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(this);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.message_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Handle menu item click here
                    int clickedPosition = getAdapterPosition(); // Get the clicked position
                    if (item.getItemId() == R.id.delete) {
                        showDeleteConfirmation(view);
                    }
                    return true;
                }
            });
            popupMenu.show();
        }

        private void showDeleteConfirmation(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete this message?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MessageRecycler message = (MessageRecycler) view.getTag(); // Retrieve the message object from the view's tag
                            deleteMessage(message);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel action or dismiss the dialog
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        private void deleteMessage(MessageRecycler message) {
            messageRepository.deleteMessage(message.getId(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("OkHTTP!", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.code() != 200) {
                        Log.e("OkHTTP!", "Not 200 error code! Uh oh...");
                        return;
                    }

                    MessageRecycler message = (MessageRecycler) itemView.getTag(); // Assuming the message object is set as the tag
                    int position = adapter.getMessages().indexOf(message);
                    if (position == -1) {
                        Log.e("deleteMessage", "Message not found in adapter");
                        return;
                    }
                    //remove message on UI thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.removeMessage(position);
                        }
                    });
                }
            });
        }


        @Override
        public void onClick(View view) {
            // Toggle visibility of dateTime TextView
            if (dateTime.getVisibility() == View.INVISIBLE) {
                dateTime.setVisibility(View.VISIBLE);
                setDateTimeVisibility(); // Set height when visible
                return;
            }
            dateTime.setVisibility(View.INVISIBLE);
            setDateTimeVisibility(); // Set height to 0 when invisible
        }

        private void setDateTimeVisibility() {
            if (dateTime.getVisibility() == View.VISIBLE) {
                // Set height to WRAP_CONTENT
                ViewGroup.LayoutParams params = dateTime.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dateTime.setLayoutParams(params);
                return;
            }
            // Set height to 0
            ViewGroup.LayoutParams params = dateTime.getLayoutParams();
            params.height = 0;
            dateTime.setLayoutParams(params);
        }
    }

    /**
     * Returns the data at a specific position.
     *
     * @param id The position of the item in the data set.
     * @return The data at the specified position.
     */
    public MessageRecycler getItem(int id) {
        return messages.get(id);
    }

    /**
     * Sets the item click listener.
     *
     * @param itemClickListener The item click listener to set.
     */
    public void setClickListener(MessageRecyclerViewAdapter.ItemClickListener itemClickListener) {
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

    public void updateItems(List<MessageRecycler> newMessages) {
        messages = newMessages;
        this.notifyDataSetChanged();
    }

    public void removeMessage(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }

    public List<MessageRecycler> getMessages() {
        return messages;
    }

    private void alignText(TextView textView, int position) {
        Spannable spannable = (Spannable) textView.getText();
        Layout layout = textView.getLayout();
        if (layout == null || layout.getLineCount() != 1)
            return;

        Layout.Alignment alignmentStart = Layout.Alignment.ALIGN_NORMAL;
        Layout.Alignment alignmentEnd = Layout.Alignment.ALIGN_OPPOSITE;

        AlignmentSpan.Standard startAlignment = new AlignmentSpan.Standard(alignmentStart);
        AlignmentSpan.Standard endAlignment = new AlignmentSpan.Standard(alignmentEnd);

        AlignmentSpan.Standard alignment = getItemViewType(position) == 1 ? endAlignment : startAlignment;
        spannable.setSpan(alignment, 0, textView.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
