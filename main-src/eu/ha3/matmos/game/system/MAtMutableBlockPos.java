package eu.ha3.matmos.game.system;

import net.minecraft.util.BlockPos;

/**
 * @author dags_ <dags@dags.me>
 */

public class MAtMutableBlockPos extends BlockPos
{
    private int x;
    private int y;
    private int z;

    public MAtMutableBlockPos()
    {
        super(0, 0, 0);
    }

    public MAtMutableBlockPos of(int xPos, int yPos, int zPos)
    {
        x = xPos;
        y = yPos;
        z = zPos;
        return this;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }
}
