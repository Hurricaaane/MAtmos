package eu.ha3.matmos.engine0.game.data.abstractions.scanner;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Signal;

/* x-placeholder */

public class MAtScanVolumetricModel
{
	private MAtScanCoordsOps pipeline;
	
	private int xstart;
	private int ystart;
	private int zstart;
	
	private int xsize;
	private int ysize;
	private int zsize;
	
	private int opspercall;
	
	private boolean isScanning;
	
	private int finality;
	private int progress;
	
	private Ha3Signal onDone;
	
	public MAtScanVolumetricModel()
	{
		this.pipeline = null;
		this.isScanning = false;
	}
	
	public void setPipeline(MAtScanCoordsOps pipelineIn)
	{
		this.pipeline = pipelineIn;
		
	}
	
	public void startScan(
		int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn, Ha3Signal onDoneIn) //throws MAtScannerTooLargeException
	{
		if (this.isScanning)
			return;
		
		if (this.pipeline == null)
			return;
		
		if (opspercallIn <= 0)
			throw new IllegalArgumentException();
		
		int worldHeight = Minecraft.getMinecraft().theWorld.getHeight();
		
		if (ysizeIn > worldHeight)
		{
			ysizeIn = worldHeight;
		}
		
		this.xsize = xsizeIn;
		this.ysize = ysizeIn;
		this.zsize = zsizeIn;
		
		y = y - this.ysize / 2;
		
		if (y < 0)
		{
			y = 0;
		}
		else if (y > worldHeight - this.ysize)
		{
			y = worldHeight - this.ysize;
		}
		
		this.xstart = x - this.xsize / 2;
		this.ystart = y; // ((y - ysize / 2)) already done before
		this.zstart = z - this.zsize / 2;
		this.opspercall = opspercallIn;
		
		this.progress = 0;
		this.finality = this.xsize * this.ysize * this.zsize;
		
		this.pipeline.begin();
		this.isScanning = true;
		
	}
	
	public boolean routine()
	{
		if (!this.isScanning)
			return false;
		
		long ops = 0;
		int x, y, z;
		while (ops < this.opspercall && this.progress < this.finality)
		{
			// TODO Optimize this
			x = this.xstart + this.progress % this.xsize;
			z = this.zstart + this.progress / this.xsize % this.zsize;
			y = this.ystart + this.progress / this.xsize / this.zsize;
			
			this.pipeline.input(x, y, z);
			
			ops++;
			this.progress++;
			
		}
		
		if (this.progress >= this.finality)
		{
			scanDoneEvent();
		}
		return true;
		
	}
	
	public void stopScan()
	{
		this.isScanning = false;
		
	}
	
	private void scanDoneEvent()
	{
		if (!this.isScanning)
			return;
		
		this.pipeline.finish();
		stopScan();
		
		if (this.onDone != null)
		{
			this.onDone.signal();
		}
		
	}
	
}
