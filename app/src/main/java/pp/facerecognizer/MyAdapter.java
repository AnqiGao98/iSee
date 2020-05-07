//package pp.facerecognizer;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
//    private ArrayList<String> mDataset;
//    private onContactListener mOnContactListener;
//
//    // Provide a reference to the views for each data item
//    // Complex data items may need more than one view per item, and
//    // you provide access to all the views for a data item in a view holder
//    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        public TextView txtViewTitle;
//        //public ImageView imgViewIcon;
//        public IMyViewHolderClicks mListener;
//
//        public MyViewHolder(View itemLayoutView, IMyViewHolderClicks listener) {
//            super(itemLayoutView);
//            mListener = listener;
//            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.textView);
//            //imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
//            //imgViewIcon.setOnClickListener(this);
//            itemLayoutView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (v instanceof ImageView){
//                mListener.onTomato((ImageView)v);
//            } else {
//                mListener.onPotato(v);
//            }
//        }
//
//        public static interface IMyViewHolderClicks {
//            public void onPotato(View caller);
//            public void onTomato(ImageView callerImage);
//        }
//    }
//
//    // Provide a suitable constructor (depends on the kind of dataset)
//    public MyAdapter(String[] myDataset) {
//        mDataset = myDataset;
//    }
//
//    // Create new views (invoked by the layout manager)
//    @Override
//    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
//                                                     int viewType) {
//        // create a new view
////        TextView v = (TextView) LayoutInflater.from(parent.getContext())
////                .inflate(R.layout.my_text_view, parent, false);
////
////        MyViewHolder vh = new MyViewHolder(v);
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_layout, parent, false);
//
//        MyAdapter.ViewHolder vh = new RecyclerView.ViewHolder(v, new MyAdapter.ViewHolder.IMyViewHolderClicks() {
//            public void onPotato(View caller) { Log.d("VEGETABLES", "Poh-tah-tos"); };
//            public void onTomato(ImageView callerImage) { Log.d("VEGETABLES", "To-m8-tohs"); }
//        });
//        return vh;
//    }
//
//    // Replace the contents of a view (invoked by the layout manager)
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        // - get element from your dataset at this position
//        // - replace the contents of the view with that element
//        holder.textView.setText(mDataset[position]);
//
//    }
//
//    // Return the size of your dataset (invoked by the layout manager)
//    @Override
//    public int getItemCount() {
//        return mDataset.length;
//    }
//
//    public interface onContactListener{
//        void onContactClick(int position);
//    }
//}