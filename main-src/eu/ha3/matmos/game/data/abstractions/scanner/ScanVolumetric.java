package eu.ha3.matmos.game.data.abstractions.scanner;

import net.minecraft.client.Minecraft;

/* x-placeholder */

public class ScanVolumetric implements Progress
{
	private ScanOperations pipeline;
	
	private int xstart;
	private int ystart;
	private int zstart;
	
	private int xsize;
	private int ysize;
	private int zsize;
	
	private int opspercall;
	
	private boolean isScanning;
	
	private int finalProgress = 1;
	private int progress = 1; // We don't want progress to be zero, to avoid divide by zero
	
	//
	
	private int xx;
	private int yy;
	private int zz;
	
	public ScanVolumetric()
	{
		this.pipeline = null;
		this.isScanning = false;
	}
	
	public void setPipeline(ScanOperations pipelineIn)
	{
		this.pipeline = pipelineIn;
	}
	
	public void startScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) //throws MAtScannerTooLargeException
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
		this.finalProgress = this.xsize * this.ysize * this.zsize;
		
		this.xx = 0;
		this.yy = 0;
		this.zz = 0;
		
		this.pipeline.begin();
		this.isScanning = true;
	}
	
	public boolean routine()
	{
		if (!this.isScanning)
			return false;
		long ops = 0;
		while (ops < this.opspercall && this.progress < this.finalProgress)
		{
			this.pipeline.input(this.xstart + this.xx, this.ystart + this.yy, this.zstart + this.zz);
			
			this.xx = (this.xx + 1) % this.xsize;
			if (this.xx == 0)
			{
				this.zz = (this.zz + 1) % this.zsize;
				if (this.zz == 0)
				{
					this.yy = this.yy + 1;
					if (this.yy >= this.ysize && this.progress != this.finalProgress - 1)
					{
						System.err.println("LOGIC ERROR");
					}
				}
			}
			
			ops++;
			this.progress++;
			
		}
		
		if (this.progress >= this.finalProgress)
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
	}
	
	@Override
	public int getProgress_Current()
	{
		return this.progress;
	}
	
	@Override
	public int getProgress_Total()
	{
		return this.finalProgress;
	}
	
}
