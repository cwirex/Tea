package com.example.tea.menu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.tea.user.friend.AddFriendActivity
import com.example.tea.user.friend.FriendsListActivity
import com.example.tea.R

class FriendsMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends_menu, container, false)
        val addFriendMenuButton = view.findViewById<Button>(R.id.addFriendMenuButton)
        val friendListButton = view.findViewById<Button>(R.id.friendsListButton)

        addFriendMenuButton.setOnClickListener {
            val intent = Intent(activity, AddFriendActivity::class.java)
            startActivity(intent)
        }

        friendListButton.setOnClickListener {
            val intent = Intent(activity, FriendsListActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
