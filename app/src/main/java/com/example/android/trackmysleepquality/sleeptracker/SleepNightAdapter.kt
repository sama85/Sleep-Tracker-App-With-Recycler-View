/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter : RecyclerView.Adapter<TextItemViewHolder>(){

    //adapter takes data to adapt to rv as a list
    var data = listOf<SleepNight>()
        set(value) {
            field = value
            //rv refreshes all displayed items according to updated list
            //can be slow with complex viewholders to obscure scrolling
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    //binds data of given position in list to given vh
    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        if(item.sleepQuality <= 1)
            holder.textView.setTextColor(Color.RED)
        //must reset color to black for high qualities bec this holder is reused and could be
        //modified by previous data
        else
            holder.textView.setTextColor(Color.BLACK)

        holder.textView.text = item.sleepQuality.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        //views must be inflated in vh when created
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false) as TextView
        //create vh with inflated views
        return TextItemViewHolder(view)
    }

    /*
    Rv works as follows:
        1. get items count
        2. create vh enough for screen size
        3. bind data from list to vh
        4. reuse vh on scrolling and repeat from step 3
     */
}