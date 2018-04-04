package com.kk.libtag

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by kevin on 03/04/2018.
 */
class TagView(context: Context, attributes: AttributeSet?, defStyle: Int) : RelativeLayout(context, attributes, defStyle) {
    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)
    constructor(context: Context) : this(context, null)

    companion object {
        val DEFAULT_LINE_MARGIN = 5f
        val DEFAULT_TAG_MARGIN = 5f
        val DEFAULT_TAG_TEXT_PADDING_LEFT = 8f
        val DEFAULT_TAG_TEXT_PADDING_TOP = 5f
        val DEFAULT_TAG_TEXT_PADDING_RIGHT = 8f
        val DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5f
    }

    private var lineMargin: Int = dip(DEFAULT_LINE_MARGIN)
    private var tagMargin: Int = dip(DEFAULT_TAG_MARGIN)
    private var textPaddingLeft: Int = dip(DEFAULT_TAG_TEXT_PADDING_LEFT)
    private var textPaddingRight: Int = dip(DEFAULT_TAG_TEXT_PADDING_RIGHT)
    private var textPaddingTop: Int = dip(DEFAULT_TAG_TEXT_PADDING_TOP)
    private var textPaddingBottom: Int = dip(DEFAULT_TAG_TEXT_PADDING_BOTTOM)

    var tagObserver: Variable<SelectTag> = Variable(SelectTag.Default, true)
    private var tagList: MutableList<Tag> = mutableListOf()
    private var mInitialized: Boolean = false
    private var mWidth: Int = 0

    init {
        context.obtainStyledAttributes(attributes, R.styleable.TagView, defStyle, defStyle).apply {
            lineMargin = getDimension(R.styleable.TagView_lineMargin, dip(DEFAULT_LINE_MARGIN).toFloat()).toInt()
            tagMargin = getDimension(R.styleable.TagView_tagMargin, dip(DEFAULT_TAG_MARGIN).toFloat()).toInt()
            textPaddingLeft = getDimension(R.styleable.TagView_textPaddingLeft, dip(DEFAULT_TAG_TEXT_PADDING_LEFT).toFloat()).toInt()
            textPaddingRight = getDimension(R.styleable.TagView_textPaddingRight, dip(DEFAULT_TAG_TEXT_PADDING_RIGHT).toFloat()).toInt()
            textPaddingTop = getDimension(R.styleable.TagView_textPaddingTop, dip(DEFAULT_TAG_TEXT_PADDING_TOP).toFloat()).toInt()
            textPaddingBottom = getDimension(R.styleable.TagView_textPaddingBottom, dip(DEFAULT_TAG_TEXT_PADDING_BOTTOM).toFloat()).toInt()
        }
        viewTreeObserver.addOnGlobalLayoutListener {
            if (!mInitialized) {
                "globalLayoutListener".print()
                mInitialized = true
                drawTags()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        "onSizeChanged $w".print()
        mWidth = w
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measuredWidth.apply {
            "onMeasure $this".print()
            if (this <= 0) return@apply
            else mWidth = this
        }
    }

    fun addTag(tag: Tag) {
        tagList.add(tag)
        drawTags()
    }

    fun addTagList(tags: List<Tag>) {
        tagList.addAll(tags)
        drawTags()
    }

    fun clear() {
        tagList.clear()
        drawTags()
    }

    private fun drawTags() {
        if (!mInitialized) return
        "drawTags mWidth: $mWidth".print()
        removeAllViews()
        var occupyWidth = paddingLeft + paddingRight
        var headIndex = 1
        var curIndex = 1
        tagList.forEach {
            it.itemView(context, tagObserver).apply {
                id = curIndex
                val width = containWidth(it)
                val tagParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply { bottomMargin = lineMargin }
                if (mWidth.print("mWidth: ") > occupyWidth.print("occupyWidth: ") + width.print("tag width: ") + tagMargin.print("tag margin: ")) {
                    tagParams.addRule(RelativeLayout.ALIGN_TOP, headIndex)
                    tagParams.addRule(RelativeLayout.RIGHT_OF, curIndex - 1)
                    tagParams.leftMargin = tagMargin
                    occupyWidth += tagMargin
                } else {
                    tagParams.addRule(RelativeLayout.BELOW, headIndex)
                    tagParams.leftMargin = tagMargin
                    occupyWidth = this@TagView.paddingLeft + this@TagView.paddingRight
                    headIndex = curIndex
                }
                occupyWidth += width.toInt()
                curIndex++
                this@TagView.addView(this, tagParams)
            }
        }
    }
}

sealed class Tag(val _text: String, val _textColor: Int, val _backColorRes: Int, val _size: Float, val _leftPadding: Int, val _topPadding: Int, val _rightPadding: Int, val _bottomPadding: Int) {
    data class TextTag(val text: String, val textColor: Int = Color.BLACK, val backColorRes: Int = R.drawable.tag_bg, val size: Float = 16f, val leftPadding: Int = 10, val topPadding: Int = 5, val rightPadding: Int = 10, val bottomPadding: Int = 5) :
            Tag(text, textColor, backColorRes, size, leftPadding, topPadding, rightPadding, bottomPadding)

    data class TextIconTag(val text: String, val textColor: Int = Color.BLACK, val backColorRes: Int = R.drawable.tag_bg, val size: Float = 16f, val leftPadding: Int = 10, val topPadding: Int = 5, val rightPadding: Int = 10, val bottomPadding: Int = 5, val leftIcon: Drawable) :
            Tag(text, textColor, backColorRes, size, leftPadding, topPadding, rightPadding, bottomPadding)
}

class SelectTag(val tag: Tag, val isSelect: Boolean) {
    companion object {
        val Default: SelectTag = SelectTag(Tag.TextTag("--"), false)
    }
}

fun Tag.itemView(context: Context, variable: Variable<SelectTag>): ItemView = ItemView(context = context).apply { bindTag(this@itemView, variable) }

class ItemView(context: Context) : LinearLayout(context) {
    private var textView: TextView = textView {
        id = View.generateViewId()
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    fun bindTag(tag: Tag, variable: Variable<SelectTag>) {
        textView.apply {
            text = tag._text
            textColor = tag._textColor
            setTextSize(TypedValue.COMPLEX_UNIT_SP, tag._size)
            setPadding(dip(tag._leftPadding), dip(tag._topPadding), dip(tag._rightPadding), dip(tag._bottomPadding))
        }
        this.setBackgroundResource(tag._backColorRes)
        this.onClick {
            isSelected = !isSelected
            variable.set(SelectTag(tag, isSelected))
        }
        tag.apply {
            when (this) {
                is Tag.TextIconTag -> {
                    leftIcon.run {
                        setBounds(0, 0, minimumWidth, minimumHeight)
                        textView.setCompoundDrawables(this, null, null, null)
                        textView.compoundDrawablePadding = dip(5)
                    }
                }
            }
        }
    }

    fun containWidth(tag: Tag): Float = tag.run {
        when (this) {
            is Tag.TextTag -> {
                textView.paint.measureText(text) + dip(leftPadding) + dip(rightPadding)
            }
            is Tag.TextIconTag -> {
                textView.paint.measureText(text) + dip(leftPadding) + dip(rightPadding) + leftIcon.minimumWidth + dip(10)
            }
        }
    }
}