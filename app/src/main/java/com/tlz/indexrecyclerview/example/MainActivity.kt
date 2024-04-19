package com.tlz.indexrecyclerview.example

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.promeg.pinyinhelper.Pinyin
import com.tlz.indexrecyclerview.Index
import com.tlz.indexrecyclerview.SampleIndex
import com.tlz.indexrecyclerview.SampleIndexRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_view.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 正常情况，索引条的索引与数据的索引一致.
        val myViewAdapter = MyViewAdapter()

        //rv.adapter = myViewAdapter

        rv.setAdapter(myViewAdapter,false)



        val arrayList = ArrayList<ItemData>()
        val listOf0 = listOf("所有人")
        arrayList.add(ItemData(listOf0, " "))
        val listOf = listOf("阿娇", "阿毛", "狗","计划","球球","1XX","susyimes")
        classifyByInitials(listOf).forEach {
            arrayList.add(it)
        }

//        arrayList.add(ItemData(listOf, "A"))
//        val listOf2 = listOf("C2", "C3", "C4")
//        arrayList.add(ItemData(listOf2, "C"))
//        arrayList.add(ItemData(listOf, "J"))
//        arrayList.add(ItemData(listOf, "K"))
//        arrayList.add(ItemData(listOf, "L"))
//        val listOf3 = listOf("N2", "N3", "N4")
//        arrayList.add(ItemData(listOf3, "N"))
//        arrayList.add(ItemData(listOf, "O"))
//        arrayList.add(ItemData(listOf, "P"))
//        arrayList.add(ItemData(listOf, "Q"))
//        arrayList.add(ItemData(listOf, "R"))
//        arrayList.add(ItemData(listOf, "S"))
//        arrayList.add(ItemData(listOf, "T"))
        myViewAdapter.setNewData(arrayList)
        // 索引条的索引比数据的索引多的情况(如：数据首字母只有A到F的索引，但右边的索引条显示的是A到Z).
//        rv.adapter = MyViewAdapter2()
        // 设置字体
        //rv.indexBarTextTypeface = Typeface.DEFAULT_BOLD
        // 自定义实现索引条背景与预览框背景，默认实现类: DefIndexDecoration
//        rv.indexDecoration = object: IndexDecoration{
//            override fun drawIndexBarBg(cvs: Canvas, drawnPaint: Paint, drawnRectF: RectF, drawnColor: Int) {
//                // 绘制自己的样式，
//            }
//
//            override fun drawPreviewBg(cvs: Canvas, drawnPaint: Paint, drawnRectF: RectF, drawnColor: Int, radius: Float) {
//                // 绘制自己的样式
//            }
//        }
    }

    fun classifyByInitials(listOfNames: List<String>): List<ItemData> {
        val map = mutableMapOf<String, MutableList<String>>()

        // 将每个字符串分类到对应的首字母Key下
        listOfNames.forEach { name ->
            val firstChar = name[0]
            val index = when {
                Pinyin.isChinese(firstChar) -> Pinyin.toPinyin(firstChar).first().toUpperCase().toString() // 中文
                firstChar.isLetter() -> firstChar.toUpperCase().toString() // 英文
                else -> "#" // 其他字符归类到 '#'
            }

            map.getOrPut(index) { mutableListOf() }.add(name)
        }

        // 创建ItemData列表，并将 '#' 分类移动到列表末尾
        val items = map.entries.sortedBy { if (it.key == "#") "ZZZ" else it.key }
            .map { (index, names) -> ItemData(names, index) }
        return items
    }

    class ItemData(val data: List<String>, index: String) : SampleIndex(index) {

        override fun getDataCount(): Int = data.size

    }

    class MyViewAdapter : SampleIndexRecyclerViewAdapter<ItemData, RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {
        override fun onBindIndexViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: ItemData) {
            holder.itemView.tv_item.setTextColor(Color.BLACK)
            holder.itemView.tv_item.text = item.index
            //" "空格用于所有人
            if (item.index == " ") {
                holder.itemView.tv_item.height = 0
            } else {
                holder.itemView.tv_item.height = 80
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: ItemData, subPosition: Int) {
            holder.itemView.tv_item.text = item.data[subPosition]
        }

        override fun onCreateIndexViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
                ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_view, parent, false))

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }


    /**
     * 索引条的索引比数据的索引多的情况.
     */
    class MyViewAdapter2 : SampleIndexRecyclerViewAdapter<ItemData, RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {

        /** 索引列表,26个字母. */
        private val indexList = Array(26) {
            Index(('A'.toInt() + it).toChar().toString())
        }.toList()



        /**
         * 重写索引列表获取，默认是data.
         * @return List<Index>
         */
        override fun getIndexList(): List<Index> = indexList

        override fun onBindIndexViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: ItemData) {

            holder.itemView.tv_item.setTextColor(Color.BLACK)
            holder.itemView.tv_item.text = item.index
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, item: ItemData, subPosition: Int) {
            holder.itemView.tv_item.text = item.data[subPosition]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header_view, parent, false))

        override fun onCreateIndexViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))

    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
