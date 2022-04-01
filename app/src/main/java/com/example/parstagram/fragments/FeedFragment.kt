package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {

    lateinit var rvPosts: RecyclerView

    lateinit var adapter: PostAdapter

    var allPosts: MutableList<Post> = mutableListOf()

    lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This is where we set up our views and click listeners
        rvPosts = view.findViewById(R.id.rvPosts)
        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing feed")
            queryPosts()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml)
        // 2. Create data source for each row (this is the Post class)
        // 3. Create adapter that will bridge data and row layout (PostAdapter)
        // 4. Set adapter on RecyclerView
        adapter = PostAdapter(requireContext(), allPosts)
        rvPosts.adapter = adapter
        // 5. Set layout manager on RecyclerView
        rvPosts.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()
    }

    // Query for all posts in our server
    open fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        // Return the posts in descending order: ie. newer posts will appear first
        query.addDescendingOrder("createdAt")

        // Only return the most recent 20 posts
        query.limit = 20

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has gone wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    // Clear out our currently fetched posts
                    adapter.clear()

                    if (posts != null) {
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: "
                                    + post.getUser()?.username)
                        }
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                    swipeContainer.isRefreshing = false
                }
            }

        })
    }

    companion object {
        const val TAG = "FeedFragment"
    }
}