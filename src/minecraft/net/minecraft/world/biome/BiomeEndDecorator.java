package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator
{
    protected WorldGenerator spikeGen = new WorldGenSpikes(Blocks.end_stone);

    protected void genDecorations(BiomeGenBase biomeGenBaseIn)
    {
        this.generateOres();

        if (this.randomGenerator.nextInt(5) == 0)
        {
            int lvt_2_1_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_3_1_ = this.randomGenerator.nextInt(16) + 8;
            this.spikeGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(lvt_2_1_, 0, lvt_3_1_)));
        }

        if (this.field_180294_c.getX() == 0 && this.field_180294_c.getZ() == 0)
        {
            EntityDragon lvt_2_2_ = new EntityDragon(this.currentWorld);
            lvt_2_2_.setLocationAndAngles(0.0D, 128.0D, 0.0D, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
            this.currentWorld.spawnEntityInWorld(lvt_2_2_);
        }
    }
}
