package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<VertexFormatElement> elements;
    private final List<Integer> offsets;

    /** The next available offset in this vertex format */
    private int nextOffset;
    private int colorElementOffset;
    private List<Integer> uvOffsetsById;
    private int normalElementOffset;

    public VertexFormat(VertexFormat vertexFormatIn)
    {
        this();

        for (int lvt_2_1_ = 0; lvt_2_1_ < vertexFormatIn.getElementCount(); ++lvt_2_1_)
        {
            this.addElement(vertexFormatIn.getElement(lvt_2_1_));
        }

        this.nextOffset = vertexFormatIn.getNextOffset();
    }

    public VertexFormat()
    {
        this.elements = Lists.newArrayList();
        this.offsets = Lists.newArrayList();
        this.nextOffset = 0;
        this.colorElementOffset = -1;
        this.uvOffsetsById = Lists.newArrayList();
        this.normalElementOffset = -1;
    }

    public void clear()
    {
        this.elements.clear();
        this.offsets.clear();
        this.colorElementOffset = -1;
        this.uvOffsetsById.clear();
        this.normalElementOffset = -1;
        this.nextOffset = 0;
    }

    @SuppressWarnings("incomplete-switch")
    public VertexFormat addElement(VertexFormatElement element)
    {
        if (element.isPositionElement() && this.hasPosition())
        {
            LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        }
        else
        {
            this.elements.add(element);
            this.offsets.add(Integer.valueOf(this.nextOffset));

            switch (element.getUsage())
            {
                case NORMAL:
                    this.normalElementOffset = this.nextOffset;
                    break;

                case COLOR:
                    this.colorElementOffset = this.nextOffset;
                    break;

                case UV:
                    this.uvOffsetsById.add(element.getIndex(), Integer.valueOf(this.nextOffset));
            }

            this.nextOffset += element.getSize();
            return this;
        }
    }

    public boolean hasNormal()
    {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset()
    {
        return this.normalElementOffset;
    }

    public boolean hasColor()
    {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset()
    {
        return this.colorElementOffset;
    }

    public boolean hasUvOffset(int id)
    {
        return this.uvOffsetsById.size() - 1 >= id;
    }

    public int getUvOffsetById(int id)
    {
        return ((Integer)this.uvOffsetsById.get(id)).intValue();
    }

    public String toString()
    {
        String lvt_1_1_ = "format: " + this.elements.size() + " elements: ";

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.elements.size(); ++lvt_2_1_)
        {
            lvt_1_1_ = lvt_1_1_ + ((VertexFormatElement)this.elements.get(lvt_2_1_)).toString();

            if (lvt_2_1_ != this.elements.size() - 1)
            {
                lvt_1_1_ = lvt_1_1_ + " ";
            }
        }

        return lvt_1_1_;
    }

    private boolean hasPosition()
    {
        int lvt_1_1_ = 0;

        for (int lvt_2_1_ = this.elements.size(); lvt_1_1_ < lvt_2_1_; ++lvt_1_1_)
        {
            VertexFormatElement lvt_3_1_ = (VertexFormatElement)this.elements.get(lvt_1_1_);

            if (lvt_3_1_.isPositionElement())
            {
                return true;
            }
        }

        return false;
    }

    public int getIntegerSize()
    {
        return this.getNextOffset() / 4;
    }

    public int getNextOffset()
    {
        return this.nextOffset;
    }

    public List<VertexFormatElement> getElements()
    {
        return this.elements;
    }

    public int getElementCount()
    {
        return this.elements.size();
    }

    public VertexFormatElement getElement(int index)
    {
        return (VertexFormatElement)this.elements.get(index);
    }

    public int getOffset(int p_181720_1_)
    {
        return ((Integer)this.offsets.get(p_181720_1_)).intValue();
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            VertexFormat lvt_2_1_ = (VertexFormat)p_equals_1_;
            return this.nextOffset != lvt_2_1_.nextOffset ? false : (!this.elements.equals(lvt_2_1_.elements) ? false : this.offsets.equals(lvt_2_1_.offsets));
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = this.elements.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.offsets.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.nextOffset;
        return lvt_1_1_;
    }
}
