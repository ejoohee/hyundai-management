package com.example.carapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carapp.R
import com.example.carapp.adapters.PostAdapter
import com.example.carapp.fragments.MyFragment
import com.example.carapp.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val posts = mutableListOf<Post>()
    private lateinit var floatingActionButton: FloatingActionButton
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val REQUEST_CODE_ADD_POST = 100
    private val REQUEST_CODE_EDIT_POST = 101

    // ActivityResultLauncher 설정 (새 포스트 추가 및 수정)
    private val addPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchPosts()  // 새 포스트 추가 후 목록 갱신
        }
    }

    private val editPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchPosts()  // 포스트 수정 후 목록 갱신
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        // Edge to Edge 설정 (UI 확장)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // WindowInsetsListener를 사용하여 시스템 바 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false) // 제목 숨김

        toolbar.setNavigationOnClickListener {
            // myFragment로 이동하는 코드
            val fragment = MyFragment() // MyFragment는 이동하려는 프래그먼트 클래스
            val transaction = supportFragmentManager.beginTransaction()

            // 프래그먼트를 삽입할 부모 컨테이너를 지정
            transaction.replace(R.id.myFragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        floatingActionButton = findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            addPostLauncher.launch(intent)
        }

        // 리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView)
        adapter = PostAdapter(posts, this::onEditClicked, this::onDeleteClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Firestore에서 데이터를 가져옴
        fetchPosts()
    }

    // 수정 버튼 클릭 시 호출되는 메서드
    private fun onEditClicked(post: Post) {
        if (post.userId == firebaseAuth.currentUser?.uid) {
            val intent = Intent(this, PostUpdateActivity::class.java).apply {
                Log.d("postListActivity", "Post ID: ${post.postId}")
                putExtra("postId", post.postId)
                putExtra("title", post.title)
                putExtra("content", post.content)
                putExtra("image", post.image)
                putExtra("date", post.date)
            }
            editPostLauncher.launch(intent)
        }
    }

    private fun onDeleteClicked(post: Post) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Post").document(post.postId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "포스트가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                val position = posts.indexOf(post)
                if (position != -1) {
                    posts.removeAt(position) // 리스트에서 삭제
                    adapter.notifyItemRemoved(position) // RecyclerView 갱신
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "포스트 삭제에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Firestore에서 포스트 목록을 가져오는 메서드
    private fun fetchPosts() {
        lifecycleScope.launch {
            try {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    firestore.collection("Post")
                        .whereEqualTo("userId", userId)  // userId 기준으로 필터링
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot != null && !querySnapshot.isEmpty) {
                                posts.clear()
                                for (document in querySnapshot.documents) {
                                    val post = document.toObject(Post::class.java)
                                    if (post != null) {
                                        posts.add(post)
                                    }
                                }
                                // 메인 스레드에서 notifyDataSetChanged 호출
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
