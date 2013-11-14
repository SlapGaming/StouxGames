package nl.stoux.stouxgames.games.cakedefence;

import nl.stoux.stouxgames.util._;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

public enum MobType {

	//Zombies
	BABY_ZOMBIE("Baby Zombie"),
	ZOMBIE("Zombie"),
	ZOMBIE_VILLAGER("Zombie Villager"),
	BABY_ZOMBIE_PIGMAN("Baby Pigman"),
	ZOMBIE_PIGMAN("Zombie Pigman"),
	
	//Skeletons
	SKELETON("Skeleton"),
	WITHER_SKELETON("Wither Skeleton"),
		
	//Slime
	SLIME("Slime"),
	MAGMA_CUBE("Magma Cube"),
	
	//Spider
	SPIDER("Spider"),
	SPIDER_JOCKEY("Spider Jockey"),
	SPIDER_WITHER_JOCKEY("Wither Jockey"),
	CAVE_SPIDER("Cave Spider"),
	
	//Misc
	CREEPER("Creeper"),
	POWERED_CREEPER("Powered Creeper"),
	SILVERFISH("Silverfish"),
	WITCH("Witch"),
	IRON_GOLEM("Iron Golem");
	
	
	private String fullname;
	
	private MobType(String fullname) {
		this.fullname = fullname;
	}
	
	@Override
	public String toString() {
		return fullname;
	}

	/*
	 * Static
	 */
	
	//Items
	private static ItemStack pigZombieSword = new ItemStack(Material.GOLD_SWORD, 1);
	private static ItemStack skeletonBow = new ItemStack(Material.BOW, 1);
	private static ItemStack witherSkeletonSword = new ItemStack(Material.STONE_SWORD, 1);
	
	public static LivingEntity[] spawnMob(MobType type, Location loc) {
		World w = _.getWorld();
		LivingEntity[] le = null;
		LivingEntity spider, skeleton;
		PigZombie pg; Zombie z; Skeleton sk;
		
		switch(type) {
		//Zombies
		case BABY_ZOMBIE:
			z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
			z.setBaby(true);
			le = leArray(z);
			break;
		case ZOMBIE:
			le = leArray(w.spawnEntity(loc, EntityType.ZOMBIE));
			break;
		case ZOMBIE_VILLAGER:
			z = (Zombie) w.spawnEntity(loc, EntityType.ZOMBIE);
			z.setVillager(true);
			le = leArray(z);
			break;
		case BABY_ZOMBIE_PIGMAN:
			pg = (PigZombie) w.spawnEntity(loc, EntityType.PIG_ZOMBIE);
			pg.setBaby(true);
			pg.setAngry(true);
			pg.getEquipment().setItemInHand(pigZombieSword);
			le = leArray(pg);
			break;
		case ZOMBIE_PIGMAN:
			pg = (PigZombie) w.spawnEntity(loc, EntityType.PIG_ZOMBIE);
			pg.setAngry(true);
			pg.getEquipment().setItemInHand(pigZombieSword);
			le = leArray(pg);
			break;
			
		//Skelly's
		case SKELETON:
			sk = (Skeleton) w.spawnEntity(loc, EntityType.SKELETON);
			sk.getEquipment().setItemInHand(skeletonBow);
			le = leArray(sk);
			break;
		case WITHER_SKELETON:
			sk = (Skeleton) w.spawnEntity(loc, EntityType.SKELETON);
			sk.setSkeletonType(SkeletonType.WITHER);
			sk.getEquipment().setItemInHand(witherSkeletonSword);
			le = leArray(sk);
			break;
			
		//Slime
		case SLIME:
			le = leArray(w.spawnEntity(loc, EntityType.SLIME));
			break;
		case MAGMA_CUBE:
			le = leArray(w.spawnEntity(loc, EntityType.MAGMA_CUBE));
			break;
			
		//Spider related
		case SPIDER:
			le = leArray(w.spawnEntity(loc, EntityType.SPIDER));
			break;
		case SPIDER_JOCKEY:
			spider = spawnMob(SPIDER, loc)[0];
			skeleton = spawnMob(SKELETON, loc)[0];
			spider.setPassenger(skeleton);
			le = leArray(spider, skeleton);
			break;
		case SPIDER_WITHER_JOCKEY:
			spider = spawnMob(SPIDER, loc)[0];
			skeleton = spawnMob(WITHER_SKELETON, loc)[0];
			spider.setPassenger(skeleton);
			le = leArray(spider, skeleton);
			break;
		case CAVE_SPIDER:
			le = leArray(w.spawnEntity(loc, EntityType.CAVE_SPIDER));
			break;
			
		//Misc
		case CREEPER:
			le = leArray(w.spawnEntity(loc, EntityType.CREEPER));
			break;
		case POWERED_CREEPER:
			Creeper c = (Creeper) w.spawnEntity(loc, EntityType.CREEPER);
			c.setPowered(true);
			le = leArray(c);
			break;
		case SILVERFISH:
			le = leArray(w.spawnEntity(loc, EntityType.SILVERFISH));
			break;
		case WITCH:
			le = leArray(w.spawnEntity(loc, EntityType.WITCH));
			break;
		
		//Friendly
		case IRON_GOLEM:
			le = leArray(w.spawnEntity(loc, EntityType.IRON_GOLEM));
			break;
		}
		return le;
	}
	
	/**
	 * Create a new LivingEntity array
	 * @param e The entity
	 * @return the array
	 */
	private static LivingEntity[] leArray(Entity e) {
		return new LivingEntity[]{(LivingEntity) e};
	}
	
	/**
	 * Create a new LivingEntity array
	 * @param e1 Entity 1
	 * @param e2 Entity 2
	 * @return The array
	 */
	private static LivingEntity[] leArray(Entity e1, Entity e2) {
		return new LivingEntity[]{(LivingEntity) e1, (LivingEntity) e2};
	}
	
	

}
