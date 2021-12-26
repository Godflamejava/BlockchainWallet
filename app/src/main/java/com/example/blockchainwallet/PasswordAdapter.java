package com.example.blockchainwallet;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    ArrayList<Passwords>  passwordsArrayList;
    Context context;

    public PasswordAdapter(Context context, ArrayList<Passwords> passwordsArrayList){
        this.context=context;
       this.passwordsArrayList=passwordsArrayList;
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.password_card_layout, parent, false);
        return new PasswordAdapter.ViewHolder(listItem);    }

    @Override
    public void onBindViewHolder(@NonNull PasswordAdapter.ViewHolder holder, int position) {
        if(position%5==0)
            holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient2));
        else  if(position%5==1)
            holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient1));
        else  if(position%5==2)
            holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient3));
       else if(position%5==3)
            holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient4));
       else
           holder.imageView.setBackground(ContextCompat.getDrawable(context,R.drawable.gradient5));

       Passwords passwords=passwordsArrayList.get(position);
       if(!passwords.getNote().equals(""))
       holder.textView.setText(passwords.getNote());
       holder.card.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent= new Intent(context,PasswordViewActivity.class);
               context.startActivity(intent);
               ((Activity)context).finish();
               SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",MODE_PRIVATE);

               SharedPreferences.Editor myEdit = sharedPreferences.edit();
               myEdit.putString("link",passwords.getImage() );
               myEdit.putString("password",passwords.getPasswords() );
               myEdit.putString("time",passwords.getTime() );
               myEdit.apply();
           }
       });




    }

    @Override
    public int getItemCount() {
        return passwordsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
       CardView card;
       TextView textView;
       ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card=itemView.findViewById(R.id.card);
            textView=itemView.findViewById(R.id.tvdescription);
            imageView=itemView.findViewById(R.id.background);

        }
    }
}
