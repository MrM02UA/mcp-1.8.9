package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator
{
    private final Cipher cipher;
    private byte[] field_150505_b = new byte[0];
    private byte[] field_150506_c = new byte[0];

    protected NettyEncryptionTranslator(Cipher cipherIn)
    {
        this.cipher = cipherIn;
    }

    private byte[] func_150502_a(ByteBuf buf)
    {
        int lvt_2_1_ = buf.readableBytes();

        if (this.field_150505_b.length < lvt_2_1_)
        {
            this.field_150505_b = new byte[lvt_2_1_];
        }

        buf.readBytes(this.field_150505_b, 0, lvt_2_1_);
        return this.field_150505_b;
    }

    protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException
    {
        int lvt_3_1_ = buffer.readableBytes();
        byte[] lvt_4_1_ = this.func_150502_a(buffer);
        ByteBuf lvt_5_1_ = ctx.alloc().heapBuffer(this.cipher.getOutputSize(lvt_3_1_));
        lvt_5_1_.writerIndex(this.cipher.update(lvt_4_1_, 0, lvt_3_1_, lvt_5_1_.array(), lvt_5_1_.arrayOffset()));
        return lvt_5_1_;
    }

    protected void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException
    {
        int lvt_3_1_ = in.readableBytes();
        byte[] lvt_4_1_ = this.func_150502_a(in);
        int lvt_5_1_ = this.cipher.getOutputSize(lvt_3_1_);

        if (this.field_150506_c.length < lvt_5_1_)
        {
            this.field_150506_c = new byte[lvt_5_1_];
        }

        out.writeBytes(this.field_150506_c, 0, this.cipher.update(lvt_4_1_, 0, lvt_3_1_, this.field_150506_c));
    }
}
