package org.iceburg.lumberjack;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LumberjackMod.MODID, name = LumberjackMod.NAME, version = LumberjackMod.VERSION)
public class LumberjackMod {
	
	public static final String MODID = "ak lumberjack mod";
	public static final String NAME = "AK Lumberjack";
	public static final String VERSION = "1.0";
    
	@Mod.Instance(MODID)
	public static LumberjackMod instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	//Handle blocks
    	System.out.println("Pre initializing Lumberjack mod");
    }
    @EventHandler
    public void init(FMLInitializationEvent event){
    	//guies, other stuff
    	System.out.println("Initializing Lumberjack mod");
    	MinecraftForge.EVENT_BUS.register(new TreeChopEventHandler());
    	System.out.println("Registered pickup items");
    	
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	//wrap up
    	System.out.println("Post initializing Lumberjack mod");
    }
    
    
}
