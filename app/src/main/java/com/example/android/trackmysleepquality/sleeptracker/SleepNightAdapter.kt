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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding


/*
Rv works as follows:
    1. get items count
    2. create vh enough for screen size
    3. bind data from list to vh
    4. reuse vh on scrolling and repeat from step 3
 */


class SleepNightAdapter : androidx.recyclerview.widget.ListAdapter<SleepNight, RecyclerView.ViewHolder>(SleepNightDiffCallback()){

    //binds data of given position in list to given vh
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(getItemViewType(position)) {
           1 -> (holder as ViewHolder).bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> ViewHolder.from(parent)
            else -> ViewHolder.from(parent)
        }
    }

    //define mapping logic between item position and view type
    //in case of facebook, how text posts, image posts and video posts for example are identified by position???
    //HOW TO USE ENUMS IN VIEW HOLDER TYPE IDENTIFICATION?
    override fun getItemViewType(position: Int): Int {
        if(position >= 0) return ViewHolderTypes.SLEEP_RECORD.getType()
        return super.getItemViewType(position)
    }

    //view/binding object should be passed to view holder so that it knows the view
    //constructor called in onCreateViewHolder
    //inflated view is passed to view holder to know which view it holds
    //define views as properties to be easily accessed in on bind view holder
    class ViewHolder(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            //inflating xml layout to vh is specific to each vh class, so should be a method in vh
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                //create vh with inflated views
                return ViewHolder(binding)
            }
        }

        fun bind(item: SleepNight) {
           binding.sleepNight = item
           //WHAT DOES IT DO?
           binding.executePendingBindings()
        }
    }

    enum class ViewHolderTypes{
        SLEEP_RECORD{
            override fun getType(): Int {
                return 1
            }
        };
        abstract fun getType() : Int
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    //HOW DETERMINES CHANGED ITEMS WHEN IT COMPARES DIFFERENT IDS?
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}