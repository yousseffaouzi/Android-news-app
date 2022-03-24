package fr.isep.news.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.isep.news.Model.Category;
import fr.isep.news.databinding.CategoryItemsBinding;

public class CategoryRecyclerVAdapter extends RecyclerView.Adapter<CategoryRecyclerVAdapter.ViewHolder>{
    private ArrayList<Category> DataSet;
    private Context context;
    private CategoryClickInterface categoryClickInterface;

    public CategoryRecyclerVAdapter(ArrayList<Category> dataSet, Context context, CategoryClickInterface categoryClickInterface) {
        DataSet = dataSet;
        this.context = context;
        this.categoryClickInterface = categoryClickInterface;
    }

    public interface CategoryClickInterface{
        void onClickCategory(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView CategoryTextView;

//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            CategoryTextView = itemView.findViewById(R.id.CategoryName);
//        }

        //Use ViewBinding
        public ViewHolder(@NonNull CategoryItemsBinding binding) {
            super(binding.getRoot());
            CategoryTextView = binding.CategoryName;
        }
    }

    @NonNull
    @Override
    public CategoryRecyclerVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

//        View view = LayoutInflater.from(viewGroup.getContext())
//                .inflate(R.layout.category_items, viewGroup, false);
//        return new ViewHolder(view);

        //Use ViewBinding
        CategoryItemsBinding binding = CategoryItemsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return  new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
           Category curentCategory = DataSet.get(position);
           holder.CategoryTextView.setText(curentCategory.getCategoryName());

           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   categoryClickInterface.onClickCategory(position);
               }
           });
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

}
