package com.young.sportsmatch.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.young.sportsmatch.data.model.BookMarkItem
import com.young.sportsmatch.data.model.Category
import com.young.sportsmatch.database.BookmarkEntity
import com.young.sportsmatch.databinding.ItemBookmarkSectionPostListBinding
import com.young.sportsmatch.ui.home.HomeViewModel

class BookmarkListAdapter(
    private val onItemClick: (BookmarkEntity?, Category?) -> Unit,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val bookMarkList = mutableListOf<BookMarkItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SECTION_ARTICLE -> BookMarkSectionArticleViewHolder(
                ItemBookmarkSectionPostListBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onItemClick,
                viewModel,
                bookMarkList
            )

            else -> throw ClassCastException("Invalid Class Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookMarkSectionArticleViewHolder -> {
                val item = bookMarkList[position] as BookMarkItem.BookMarkSection
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (bookMarkList[position]) {
            is BookMarkItem.BookMarkSection -> VIEW_TYPE_SECTION_ARTICLE
        }
    }

    override fun getItemCount(): Int {
        return bookMarkList.size
    }

    fun submitList(data: List<BookMarkItem>) {
        bookMarkList.clear()
        bookMarkList.addAll(data)
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_SECTION_ARTICLE = 1
    }

    class BookMarkSectionArticleViewHolder(
        private val binding: ItemBookmarkSectionPostListBinding,
        onItemClick: (BookmarkEntity?, Category?) -> Unit,
        private val viewModel: HomeViewModel,
        private val bookMarkList: List<BookMarkItem>
    ) : RecyclerView.ViewHolder(binding.root) {
        private var adapter = BookmarkAdapter(viewModel) { bookmarkEntity ->
            onItemClick(bookmarkEntity, getCategory(adapterPosition))
        }

        init {
            binding.rvBookmarkPostSection.adapter = adapter
            itemView.setOnClickListener {
                onItemClick(getBookmarkEntity(adapterPosition), getCategory(adapterPosition))
            }
        }

        fun bind(item: BookMarkItem.BookMarkSection) {
            binding.tvBookmarkPostSectionTitle.text = item.id
            adapter.submitList(item.bookMarkList)
        }

        private fun getBookmarkEntity(position: Int): BookmarkEntity? {
            if (position >= 0 && position < bookMarkList.size) {
                val section = bookMarkList[position]
                if (section is BookMarkItem.BookMarkSection) {
                    val bookMarkList = section.bookMarkList
                    if (bookMarkList.isNotEmpty()) {
                        return bookMarkList[0]
                    }
                }
            }
            return null
        }

        private fun getCategory(position: Int): Category? {
            if (position >= 0 && position < bookMarkList.size) {
                val section = bookMarkList[position]
                if (section is BookMarkItem.BookMarkSection) {
                    return Category.valueOf(section.id)
                }
            }
            return null
        }
    }
}
