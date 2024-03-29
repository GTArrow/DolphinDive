package com.capstone.dolphindive;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.dolphindive.utility.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.hanks.library.bang.SmallBangView;

public class SocialPlatform extends Fragment {
        @Nullable

        private ImageView appName;
        private ImageView appIcon;
        private ImageView red_dot;
        private RecyclerView postList;
        private ImageButton Notification;
        private ImageButton AddNewPostButton;
        private DatabaseReference PostsRef;
        private FirebaseAuth mAuth;
        private Query PostsRec;
        private String current_user_id;
        private String current_user_image;
        private DatabaseReference UsersRef;

        View view;
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            getUserProfileImg();

            view = inflater.inflate(R.layout.activity_social_platform, container, false);

            AddNewPostButton = (ImageButton) view.findViewById(R.id.add_new_post);
            AddNewPostButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent addNewPostIntent = new Intent(getActivity(), PostActivity.class);
                    startActivity(addNewPostIntent);
                }
            });

            red_dot = (ImageView) view.findViewById(R.id.red_dot);
            if (current_user_id.equals("Mz459IRSlfgGZMdTVmtMbnnFSUq2")){
                red_dot.setVisibility(View.VISIBLE);
            }

            Notification = (ImageButton) view.findViewById(R.id.notification);
            Notification.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent NotifyIntent = new Intent(getActivity(), NotifyActivity.class);
                    startActivity(NotifyIntent);
                }
            });

            PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
            PostsRec = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("timestamp");

            postList = (RecyclerView) view.findViewById(R.id.post_list);
            postList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            postList.setLayoutManager(linearLayoutManager);

            DisplayAllUsersPosts();

            return view;
        }


    private void getUserProfileImg() {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
//                    String userFullName = dataSnapshot.child("username").getValue().toString();
                    String userProfileImg = dataSnapshot.child("imageURL").getValue().toString();
                    current_user_image = userProfileImg;
//                    current_user_name =  userFullName;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

        private void DisplayAllUsersPosts(){
            FirebaseRecyclerOptions<Posts> options =
                    new FirebaseRecyclerOptions.Builder<Posts>()
                            .setQuery(PostsRec, Posts.class)
                            .build();

            FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {
                            holder.fullName.setText(model.getFullname());
                            holder.date.setText(model.getDate());
                            holder.description.setText(model.getDescription());
                            holder.time.setText(model.getTime());
                            holder.likes.setText(String.valueOf(Integer.parseInt(model.getLikes()) + Integer.parseInt(model.getNewLikes())));
                            holder.commentCounter.setText(model.getCommentCounter());
//                            holder.uid.setText(model.getUid());
                            Picasso.get().load(model.getProfileimage()).into(holder.profileImage);
                            Picasso.get().load(model.getPostimage()).into(holder.postImage);

                            if ((model.newLiker + model.liker).contains(current_user_id+" ")){
                                holder.heart.setSelected(true);
                                holder.heart.setScaleX((float) 0.7);
                                holder.heart.setScaleY((float) 0.7);
                            }

                            final DatabaseReference postRef = getRef(position);
                            DatabaseReference userPostRef = PostsRef.child(postRef.getKey());

                            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String uid = model.getUid();
                                    if(! TextUtils.equals(uid,current_user_id)){
                                        Intent intent =new Intent(getActivity(),Social_Profile.class);
                                        intent.putExtra("uid",uid);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getActivity(), "Click on other's portait to view their profile",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            holder.comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent checkComment = new Intent(getActivity(), CommentActivity.class);
                                    checkComment.putExtra("PostID", postRef.getKey());
                                    checkComment.putExtra("fullName", model.getFullname());
                                    checkComment.putExtra("date", model.getDate());
                                    checkComment.putExtra("description", model.getDescription());
                                    checkComment.putExtra("time", model.getTime());
                                    checkComment.putExtra("likes", String.valueOf(Integer.parseInt(model.getLikes()) + Integer.parseInt(model.getNewLikes())));
                                    checkComment.putExtra("commentCounter", model.getCommentCounter());
                                    checkComment.putExtra("profileImage", model.getProfileimage());
                                    checkComment.putExtra("postImage", model.getPostimage());
                                    checkComment.putExtra("userProfileImage", current_user_image);
                                    startActivity(checkComment);
                                }
                            });
                            holder.heart.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (holder.heart.isSelected()) {
//                                                holder.heart.setSelected(false);

                                            } else {
                                                userPostRef.runTransaction(new Transaction.Handler() {
                                                    @NonNull
                                                    @Override
                                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                        Posts p = currentData.getValue(Posts.class);
                                                        if (p==null){ return Transaction.success(currentData);}
                                                        p.setNewLikes(String.valueOf(Integer.parseInt(p.getNewLikes())+1));
                                                        p.setNewLiker(p.getNewLiker()+current_user_id+" ");
                                                        currentData.setValue(p);
                                                        return Transaction.success(currentData);
                                                    }

                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                        holder.heart.setSelected(true);
                                                        holder.heart.likeAnimation();
                                                        String TAG = "SocialPlatform";
                                                        Log.d("TAG", "postTransaction:onComplete:" + error);
                                                    }
                                                });
                                            }
                                        }
                                    });

                        }

                        @NonNull
                        @Override
                        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                            PostsViewHolder viewHolder = new PostsViewHolder(view);

                            return viewHolder;
                        }
                    };
            postList.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }

        public static class PostsViewHolder extends RecyclerView.ViewHolder{
            TextView fullName, date, description, time, likes, commentCounter, uid;
            CircleImageView profileImage;
            ImageView postImage;
            SmallBangView heart;
            ImageButton comment;

            public PostsViewHolder(@NonNull View itemView) {
                super(itemView);
                fullName = itemView.findViewById(R.id.post_user_name);
                date = itemView.findViewById(R.id.post_date);
                description = itemView.findViewById(R.id.description);
                time  = itemView.findViewById(R.id.post_time);
//                uid = date = itemView.findViewById(R.id.post_uid);
                postImage = itemView.findViewById(R.id.post_image);
                profileImage = itemView.findViewById(R.id.post_profile_image);
                likes = itemView.findViewById(R.id.likeCounter);
                heart = itemView.findViewById(R.id.imageViewAnimation);
                commentCounter = itemView.findViewById(R.id.commentCounter);
                comment = itemView.findViewById(R.id.commentIcon);
            }
        }

    }