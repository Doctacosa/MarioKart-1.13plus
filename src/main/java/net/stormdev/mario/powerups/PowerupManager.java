package net.stormdev.mario.powerups;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.useful.ucars.ucars;
import com.useful.ucarsCommon.StatValue;

import net.stormdev.mario.hotbar.HotBarSlot;
import net.stormdev.mario.hotbar.MarioHotBar;
import net.stormdev.mario.mariokart.MarioKart;
import net.stormdev.mario.races.Race;
import net.stormdev.mario.races.RaceType;
import net.stormdev.mario.sound.MarioKartSound;

public class PowerupManager {
	MarioKart plugin = null;
	Boolean enabled = true;
	public ItemStack respawn = null;
	private ArrayList<TNTPrimed> tntlist = new ArrayList<TNTPrimed>();

	public PowerupManager(MarioKart plugin) {
		this.plugin = plugin;
		enabled = MarioKart.config.getBoolean("mariokart.enable");
		this.respawn = new ItemStack(Material.EGG);
		ItemMeta meta = this.respawn.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Respawn");
		this.respawn.setItemMeta(meta);
	}
	
	public boolean isPowerup(ItemStack stack) {
		if(BananaPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(BlueShellPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(BombPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(BooPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(BoxPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(GreenShellPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(LightningPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(MushroomPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(PowPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(RedShellPowerup.isItemSimilar(stack)){
			return true;
		}
		else if(StarPowerup.isItemSimilar(stack)){
			return true;
		}
		return false;
	}

	public void calculate(final Player player, Event event) {
		if (!enabled) {
			return;
		}
		if (plugin.raceMethods.inAGame(player, false) == null) {
			return;
		}
		final Race race = plugin.raceMethods.inAGame(player, false);
		Boolean timed = race.getType() == RaceType.TIME_TRIAL;
		// Start calculations
		if (event instanceof PlayerInteractEvent) {
			PlayerInteractEvent evt = (PlayerInteractEvent) event;
			if (!ucars.listener.inACar(evt.getPlayer())) {
				//Spamming respawn can produce problems! So lets do stuff against that
				if((evt.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || evt
						.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) &&
						evt.getPlayer().getInventory().getItemInMainHand().equals(this.respawn))
							evt.setCancelled(true);
				return;
			}
			if (player.hasMetadata("kart.rolling")) {
				return;
			}
			Entity e = evt.getPlayer().getVehicle();
			if(!(evt.getPlayer().getVehicle() instanceof Minecart)){
				while(e != null && !(e instanceof Minecart) && e.getVehicle() != null){
					e = e.getVehicle();
				}
				if(!(e instanceof Minecart)){
					return;
				}
			}
			final Minecart car = (Minecart) e;
			if ((evt.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_AIR || evt
					.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)
					&& !timed) {
				ItemStack inHand = evt.getPlayer().getInventory().getItemInMainHand();
				// Secondary use			
				Powerup powerup = null;
				if(GreenShellPowerup.isItemSimilar(inHand)){
					powerup = new GreenShellPowerup();
				} else if(BananaPowerup.isItemSimilar(inHand)){
					powerup = new BananaPowerup();
				} else if(BombPowerup.isItemSimilar(inHand)) {
					powerup = new BombPowerup();
				}
				
				if(powerup != null){
					powerup.setOwner(player.getName());
					powerup.doLeftClickAction(race.getUser(player), player, car, car.getLocation(), race, inHand);
				}
			}
			if (!(evt.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR || evt
					.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
				return;
			}
			final ItemStack inHand = evt.getPlayer().getInventory().getItemInMainHand();
			Player ply = evt.getPlayer();
			if (inHand.equals(this.respawn)) {
				if (!car.hasMetadata("car.frozen")) {
					player.sendMessage(ChatColor.GREEN + "Respawning...");
					plugin.raceMethods.playerRespawn(player,car);
				}
				evt.setCancelled(true);
				return;
			}
			MarioHotBar hotBar = MarioKart.plugin.hotBarManager.getHotBar(ply);
			if (hotBar.getDisplayedItem(HotBarSlot.UTIL) != null
					&& player.getInventory().getHeldItemSlot() == 7) {
				MarioKart.plugin.hotBarManager.executeClick(ply, hotBar, HotBarSlot.UTIL);
				evt.setCancelled(true);
				return;
			} else if (hotBar.getDisplayedItem(HotBarSlot.SCROLLER) != null
					&& player.getInventory().getHeldItemSlot() == 6) {
				MarioKart.plugin.hotBarManager.executeClick(ply, hotBar, HotBarSlot.SCROLLER);
				evt.setCancelled(true);
				return;
			}
			if (timed) {
				return;
			}
			Powerup powerup = null;
			
			if(BananaPowerup.isItemSimilar(inHand)){
				powerup = new BananaPowerup();
			}
			else if(BlueShellPowerup.isItemSimilar(inHand)){
				powerup = new BlueShellPowerup();
			}
			else if(BombPowerup.isItemSimilar(inHand)){
				powerup = new BombPowerup();
			}
			else if(BooPowerup.isItemSimilar(inHand)){
				powerup = new BooPowerup();
			}
			else if(BoxPowerup.isItemSimilar(inHand)){
				powerup = new BoxPowerup();
			}
			else if(GreenShellPowerup.isItemSimilar(inHand)){
				powerup = new GreenShellPowerup();
			}
			else if(LightningPowerup.isItemSimilar(inHand)){
				powerup = new LightningPowerup();
			}
			else if(MushroomPowerup.isItemSimilar(inHand)){
				powerup = new MushroomPowerup();
			}
			else if(PowPowerup.isItemSimilar(inHand)){
				powerup = new PowPowerup();
			}
			else if(RedShellPowerup.isItemSimilar(inHand)){
				powerup = new RedShellPowerup();
			}
			else if(StarPowerup.isItemSimilar(inHand)){
				powerup = new StarPowerup();
			}
			
			if(powerup != null){
				powerup.setOwner(player.getName());
				powerup.doRightClickAction(race.getUser(player), player, car, car.getLocation(), race, inHand);
			}
			evt.getPlayer().getInventory().setItemInMainHand(inHand);
			evt.getPlayer().updateInventory(); // Fix 1.6 bug with inventory not
												// updating
		} else if (event instanceof VehicleUpdateEvent) {
			VehicleUpdateEvent evt = (VehicleUpdateEvent) event;
			Minecart car = (Minecart) evt.getVehicle();
			if (timed) {
				return;
			}
			
			Location signLoc = null;
			List<Entity> near = car.getNearbyEntities(0.0, 1, 0.0);		//Get all entities near/in vehicle
			Entity crystal = null;
			for(Entity e:near) {
				if(e.getType().equals(EntityType.ENDER_CRYSTAL)){		//For itemboxes
					signLoc = e.getLocation().clone().add(0,-2,0);		//Sign should be 2 Blocks underneath crystal
					crystal = e;
				}
				
				if(e.getType().equals(EntityType.PRIMED_TNT)) {			//If bomb (impact -> explosion)
					TNTPrimed tnt = (TNTPrimed) e;
					
					if(tntlist.contains(tnt) && tnt.getFuseTicks() <= 50) {	//Prevent drivers from immediately blowing up in their own bombs
						tnt.setFuseTicks(0); //explode.
					}
				}
			}
			
			if (signLoc != null) {	
				Sign sign = null;
				try {
					sign = (Sign) signLoc.getBlock().getState();
				} catch (Exception e) {
					return;
				}
				
				final String[] lines = sign.getLines();
				if (ChatColor.stripColor(lines[0]).equalsIgnoreCase(
						"[MarioKart]")
						|| ChatColor.stripColor(lines[0]).equalsIgnoreCase(
								"[uRace]")) {
					if (ChatColor.stripColor(lines[1])
							.equalsIgnoreCase("items")) {
						if (player.hasMetadata("kart.rolling")) {
							return;
						}
						final Race r = race;
						signLoc = sign.getLocation();
						if (r.reloadingItemBoxes.contains(signLoc)) {
							return; // Box is reloading
						}
						/*
						 * if(ChatColor.stripColor(lines[3]).equalsIgnoreCase("wait"
						 * )){ return; }
						 */
												
						if (player.getInventory().getStorageContents()[0] != null) {
							return; // Has item already
						}
						
						if (player.getInventory().getContents().length > 0) {
							player.getInventory().clear();
							MarioKart.plugin.hotBarManager.updateHotBar(player);
						}
						ItemStack give = null;
						if (ChatColor.stripColor(lines[2]).equalsIgnoreCase(
								"all")) {
							// Give all items
							ItemStack a = this.getRandomPowerup();
							ItemStack b = this.getRandomBoost();
							int randomNumber = plugin.random.nextInt(3);
							if (randomNumber < 1) {
								give = b;
							} else {
								give = a;
							}
							Player ply = (Player) evt.getVehicle().getPassengers().get(0);
							if (race != null) {
								if (ply.getName().equals(race.winning)) {
									while (BlueShellPowerup.isItemSimilar(give)) {
										give = this.getRandomPowerup();
									}
								}
							}
						} else {
							// Give mario items
							Player ply = (Player) evt.getVehicle().getPassengers().get(0);
							give = this.getRandomPowerup();
							if (race != null) {
								if (ply.getName().equals(race.winning)) {
									while (BlueShellPowerup.isItemSimilar(give)) {
										give = this.getRandomPowerup();
									}
								}
							}
						}
						final Player ply = (Player) evt.getVehicle().getPassengers().get(0);
						ply.setMetadata("kart.rolling", new StatValue(true,
								plugin));
						final ItemStack get = give;
						plugin.getServer().getScheduler()
								.runTaskAsynchronously(plugin, new Runnable() {

									@Override
									public void run() {
										int min = 0;
										int max = 20;
										int delay = 100;
										int z = plugin.random
												.nextInt(max - min) + min;
										for (int i = 0; i <= z; i++) {
											if(!race.getUser(ply).isFinished()) {
												ply.getInventory().clear();
												MarioKart.plugin.hotBarManager.updateHotBar(player);
												ply.getInventory().addItem(
														getRandomPowerup());
												ply.updateInventory();
												MarioKart.plugin.musicManager.playCustomSound(ply, MarioKartSound.ITEM_SELECT_BEEP);
												try {
													Thread.sleep(delay);
												} catch (InterruptedException e) {
												}
												delay = delay + (z / 100 * i);
												if (delay > 1000) {
													delay = 1000;
												}
											}
										}
										ply.getInventory().clear();
										MarioKart.plugin.hotBarManager.updateHotBar(ply);
										ply.getInventory().addItem(get);
										ply.removeMetadata("kart.rolling",
												plugin);
										ply.updateInventory();
										return;
									}
								});
						r.reloadingItemBoxes.add(signLoc);
						MarioKart.plugin.raceScheduler.updateRace(r);
						Location cLoc = null;
						
						cLoc = crystal.getLocation();
						crystal.remove();
						
						if (cLoc == null) {
							// Set crystal spawn loc from signLoc
							cLoc = signLoc.clone().add(0, 2.4, 0);
						}
						final Location loc = cLoc;
						final Location signFLoc = signLoc;
						plugin.getServer().getScheduler()
								.runTaskLater(plugin, new Runnable() {

									@Override
									public void run() {
										if (!r.reloadingItemBoxes
												.contains(signFLoc)) {
											return; // ItemBox has been
													// respawned
										}
										Chunk c = loc.getChunk();
										if (!c.isLoaded()) {
											c.load(true);
										}
										r.reloadingItemBoxes.remove(signFLoc);
										spawnItemPickupBox(loc);
										MarioKart.plugin.raceScheduler.updateRace(r);
										return;
									}
								}, 100l);
					}
				}
			}
		}
		// End calculations
		return;
	}

	public ItemStack getRandomBoost() {
		return getRandomPowerup(); //No longer support uCars items
	}

	/*
	public ItemStack getRandomPowerup() {
		PowerupType[] pows = PowerupType.values();
		int min = 0;
		int max = pows.length;
		int randomNumber = plugin.random.nextInt(max - min) + min;
		PowerupType pow = pows[randomNumber];
		Integer[] amts = new Integer[] { 1, 1, 1, 1, 1, 1, 1, 3, 1 };
		min = 0;
		max = amts.length - 1;
		if (min < 1) {
			min = 0;
		}
		if (max < 1) {
			max = 0;
		}
		randomNumber = plugin.random.nextInt(max - min) + min;
		return PowerupMaker.getPowerup(pow, amts[randomNumber]);
	}
	*/
	public ItemStack getRandomPowerup() {
		List<Class<? extends Powerup>> pows = new ArrayList<Class<? extends Powerup>>();
		pows.add(RedShellPowerup.class);
		pows.add(BlueShellPowerup.class);
		pows.add(GreenShellPowerup.class);
		pows.add(BananaPowerup.class);
		pows.add(BombPowerup.class);
		pows.add(BooPowerup.class);
		pows.add(BoxPowerup.class);
		pows.add(LightningPowerup.class);
		pows.add(MushroomPowerup.class);
		pows.add(PowPowerup.class);
		pows.add(StarPowerup.class);
		Class<? extends Powerup> rand = pows.get(MarioKart.plugin.random.nextInt(pows.size()));
		
		Powerup power = null;
		try {
			power = rand.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return new ItemStack(Material.STONE);
		}
		
		ItemStack i = power.getNewItem();
		
		return i;
	}
	
	public ItemStack getPowerup(String wanted) {
		List<Class<? extends Powerup>> pows = new ArrayList<Class<? extends Powerup>>();
		pows.add(RedShellPowerup.class);
		pows.add(BlueShellPowerup.class);
		pows.add(GreenShellPowerup.class);
		pows.add(BananaPowerup.class);
		pows.add(BombPowerup.class);
		pows.add(BooPowerup.class);
		pows.add(BoxPowerup.class);
		pows.add(LightningPowerup.class);
		pows.add(MushroomPowerup.class);
		pows.add(PowPowerup.class);
		pows.add(StarPowerup.class);
		
		Powerup power = null;
		for(Class<? extends Powerup> pow : pows) {
			if(pow.getName().toLowerCase().contains(wanted)) {
				try {
					power = pow.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					return new ItemStack(Material.STONE);
				}
			}
		}
		if(power == null) {
			return null;
		}
		
		ItemStack i = power.getNewItem();
		
		return i;
	}
	
	
	public Boolean isPlayerImmune(Player player){
		return player.hasMetadata("kart.immune");
	}
	
	public Boolean isCarImmune(Entity carBase){
		return carBase.hasMetadata("kart.immune");
	}
	
	public boolean spawnItemPickupBox(Location location){
		Bukkit.getScheduler().runTaskLater(MarioKart.plugin, new Runnable(){
			@Override
			public void run() {
				location.getChunk().load(true); //Make sure it's loaded
				return;
			}
		}, 4l);
		
		Location signLoc = location.clone();
		boolean foundSign = false;
		
		for(int i=5; i>0 && !foundSign; i--){
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			Location l = signLoc.clone();
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.NORTH).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.EAST).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.SOUTH).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.SOUTH).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.WEST).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.WEST).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.NORTH).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.NORTH).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = signLoc.getBlock().getRelative(BlockFace.EAST).getLocation();
			if(signLoc.getBlock().getType().name().toLowerCase().contains("sign")){
				foundSign = true;
				continue;
			}
			
			signLoc = l.getBlock().getRelative(BlockFace.DOWN).getLocation();
		}
		
		if(!foundSign){
			Bukkit.broadcast("Unregistered item box! If this was not intended, please report it as a bug!","mariokart.raceadmin");
			return false; //No sign, so remove it
		}
		
		final Location sgnLoc = signLoc;
		Bukkit.getScheduler().runTaskLater(MarioKart.plugin, new Runnable(){
			@Override
			public void run() {
				sgnLoc.getChunk().load(); //Make sure it's loaded
				

				Location above = sgnLoc.add(0, 2.05, 0);
				EnderCrystal newC = (EnderCrystal) above.getWorld().spawnEntity(above,
						EntityType.ENDER_CRYSTAL);
				newC.setShowingBottom(false);
				
				List<Entity> previous = newC.getNearbyEntities(0, 3, 0);
				for(Entity e:previous){ //Remove old item boxes
					if(e.getType().equals(EntityType.ENDER_CRYSTAL) && !e.equals(newC)){
						e.remove();
					}
				}
				newC.setMetadata("race.pickup", new StatValue(true, plugin));
				
				return;
			}
		}, 4l);
		return true;
	}
	
	public void addTNT(TNTPrimed tnt) {
		this.tntlist.add(tnt);
	}
	
	public void removeTNT(TNTPrimed tnt) {
		this.tntlist.remove(tnt);
	}
	
	/*
	public void spawnItemPickupBox(Location previous, Boolean force) {
		Location newL = previous;
		newL.getChunk(); // Load chunk
		Location signLoc = null;
		if ((newL.add(0, -2.4, 0).getBlock().getState() instanceof Sign)
				|| force) {
			signLoc = newL.add(0, -2.4, 0);
		} else {
			if (force) {
				double ll = newL.getY();
				Boolean foundSign = false;
				Boolean cancel = false;
				while (!foundSign && !cancel) {
					if (ll < newL.getY() - 4) {
						cancel = true;
					}
					Location i = new Location(newL.getWorld(), newL.getX(), ll,
							newL.getZ());
					if (i.getBlock().getState() instanceof Sign) {
						foundSign = true;
						signLoc = i;
					}
				}
				if (!foundSign) {
					return; // Let is be destroyed
				}
			} else {
				return; // Let them destroy it
			}
		}
		Location above = signLoc.add(0, 3.8, 0);
		EnderCrystal newC = (EnderCrystal) above.getWorld().spawnEntity(above,
				EntityType.ENDER_CRYSTAL);
		above.getBlock().setType(Material.COAL_BLOCK);
		above.getBlock().getRelative(BlockFace.WEST)
				.setType(Material.COAL_BLOCK);
		above.getBlock().getRelative(BlockFace.NORTH)
				.setType(Material.COAL_BLOCK);
		above.getBlock().getRelative(BlockFace.NORTH_WEST)
				.setType(Material.COAL_BLOCK);
		newC.setFireTicks(0);
		newC.setMetadata("race.pickup", new StatValue(true, plugin));
	}
	*/
}
