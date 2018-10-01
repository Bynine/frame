package main;

public class ShopMenu extends Inventory {
	
	private final String shopPrefix = "SHOP_";

	ShopMenu(){
	}
	
	@Override
	public void open(){
		items.clear();
		descs.clear();
		addIfNotPurchased("SHOVEL");
		addIfNotPurchased("SHELL3");
		addIfNotPurchased("WATERINGCAN");
		addIfNotPurchased("SEED1");
		super.open();
	}
	
	/**
	 * Adds an item to the shop menu only if the player hasn't purchased it before.
	 */
	private void addIfNotPurchased(String id){
		if (!FrameEngine.getSaveFile().getFlag(shopPrefix + id)){
			items.add(id);
		}
	}

	@Override
	protected void selectItem() {
		ItemDescription desc = (ItemDescription)getActiveButton().getOutput();
		if (desc.tooExpensive()){
			AudioHandler.playSound(error);
		}
		else{
			AudioHandler.playSound(select);
			FrameEngine.getSaveFile().addMoney(-desc.price);
			String id = desc.id;
			FrameEngine.getSaveFile().setFlag(shopPrefix + id, true);
			FrameEngine.getInventory().addItem(id);
			removeItem(id);
			cursor = 0;
		}
		if (outOfStock()){
			FrameEngine.endShop();
		}
	}
	
	public boolean outOfStock(){
		return items.size() == 0;
	}

}