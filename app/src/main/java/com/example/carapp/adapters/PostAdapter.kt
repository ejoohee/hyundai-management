package com.example.carapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carapp.R
import com.example.carapp.activities.PostUpdateActivity
import com.example.carapp.models.Post
import com.google.firebase.auth.FirebaseAuth

class PostAdapter(
    private val posts: List<Post>,
    private val onEditClicked: (Post) -> Unit,
    private val onDeleteClicked: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.titleTextView)
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val buttonEdit: Button = view.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // 제목과 날짜 설정
        holder.textView.text = post.title
        holder.dateTextView.text = post.date

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // 수정 버튼 설정
        if (post.userId == currentUserId) {
            holder.buttonEdit.visibility = View.VISIBLE
            holder.buttonEdit.setOnClickListener {
                onEditClicked(post) // onEditClicked 콜백 호출
            }
        } else {
            holder.buttonEdit.visibility = View.GONE
        }

        // 삭제 버튼 설정
        if (post.userId == currentUserId) {
            holder.buttonDelete.visibility = View.VISIBLE
            holder.buttonDelete.setOnClickListener {
                onDeleteClicked(post) // onDeleteClicked 콜백 호출
            }
        } else {
            holder.buttonDelete.visibility = View.GONE
        }
    }
}
