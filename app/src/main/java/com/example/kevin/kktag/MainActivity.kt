package com.example.kevin.kktag

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.example.libtag.Tag
import com.example.libtag.print
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list: MutableList<Tag> = mutableListOf()

        (1..10).forEach {
            if (it % 2 == 0) list.add(Tag.TextTag("tagtagtagtagindex $it"))
            else list.add(Tag.TextTag("tag$it"))
        }
        list.add(Tag.TextIconTag("addNewTag", leftIcon = ContextCompat.getDrawable(this, R.drawable.abc_ab_share_pack_mtrl_alpha)))
        tagView.addTagList(list)
        tagView.tagObserver.observer {
            it.tag.apply {
                when (this) {
                    is Tag.TextTag -> {
                        text.print("Text Tag isSelected: ${it.isSelect}")
                    }
                    is Tag.TextIconTag -> {
                        text.print("Icon Tag isSelected: ${it.isSelect}")
                    }
                }
            }
        }
    }
}
