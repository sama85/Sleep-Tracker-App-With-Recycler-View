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

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import java.lang.ClassCastException


/*
Rv works as follows:
    1. get items count
    2. create vh enough for screen size
    3. bind data from list to vh
    4. reuse vh on scrolling and repeat from step 3
 */


class SleepNightAdapter(val listener: SleepNightListener) :
    androidx.recyclerview.widget.ListAdapter<DataItem, RecyclerView.ViewHolder>(
        SleepNightDiffCallback()
    ) {

    //WHY IN ANOTHER SCOPE WITH COROUTINES?
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        val items = when (list) {
            null -> listOf(DataItem.HeaderItem)
            else -> listOf(DataItem.HeaderItem) + list.map { DataItem.sleepNightItem(it) }
        }
        submitList(items)
    }

    //binds data of given position in list to given vh
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SleepNightViewHolder -> {
                val nightItem = item as DataItem.sleepNightItem
                holder.bind(nightItem.sleepNight, listener)
            }
        }
    }

    //view type is obtained from getItemViewType()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewTypes.HEADER.getType() -> HeaderViewHolder.from(parent)
            ViewTypes.SLEEP_NIGHT.getType() -> SleepNightViewHolder.from(parent)
            else -> throw ClassCastException("Unknown view type ${viewType}")
        }
    }

    /*
    define mapping logic between item position and view type
    in case of facebook, how text posts, image posts and video posts for example are identified by position???
    */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.HeaderItem -> ViewTypes.HEADER.getType()
            is DataItem.sleepNightItem -> ViewTypes.SLEEP_NIGHT.getType()
        }
    }

    /*
    view/binding object should be passed to view holder so that it knows the view
    constructor called in onCreateViewHolder
    inflated view is passed to view holder to know which view it holds
    define views as properties to be easily accessed in on bind view holder
     */
    class SleepNightViewHolder(val binding: ListItemSleepNightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            //inflating xml layout to vh is specific to each vh class, so should be a method in vh
            fun from(parent: ViewGroup): SleepNightViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                //create vh with inflated views
                return SleepNightViewHolder(binding)
            }
        }

        fun bind(item: SleepNight, listener: SleepNightListener) {
            binding.root.setOnClickListener {
                listener.onSleepNightClicked(item)
            }
            binding.sleepNight = item
            //WHAT DOES IT DO?
            binding.executePendingBindings()
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

interface SleepNightListener {
    //takes night and displays data
    fun onSleepNightClicked(night: SleepNight)
}

//each item need a unique id for diffUtil
sealed class DataItem() {

    abstract val Id: Long

    class sleepNightItem(val sleepNight: SleepNight) : DataItem() {
        override val Id = sleepNight.nightId
    }

    //only 1 instance of header (list header)
    object HeaderItem : DataItem() {
        override val Id = Long.MIN_VALUE
    }
}

enum class ViewTypes {
    SLEEP_NIGHT {
        override fun getType() = 1
    },
    HEADER {
        override fun getType() = 0
    };

    abstract fun getType(): Int
}