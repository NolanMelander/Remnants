package com.remnants.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.remnants.game.Component;
import com.remnants.game.Entity;
import com.remnants.game.InventoryItem;
import com.remnants.game.InventoryItemFactory;
import com.remnants.game.InventoryItem.ItemUseType;
import com.remnants.game.InventoryItem.ItemTypeID;
import com.remnants.game.Utility;
import com.remnants.game.inventory.InventoryState;

/*
* Items are added to the inventory through the InventorySlotTarget and InventorySlotSource classes
*
*
 */
public class InventoryUI extends Window implements InventorySubject, InventorySlotObserver{
    private static final String TAG = InventoryUI.class.getSimpleName();

    public final static int _numSlots = 30;
    public static final String PLAYER_INVENTORY = "Player_Inventory";
    public static final String STORE_INVENTORY = "Store_Inventory";

    private int _lengthSlotRow = 10;
    private Table _inventorySlotTable;
    private Table _playerSlotsTable;
    private Table _equipSlots;
    private DragAndDrop _dragAndDrop;
    private Array<Actor> _inventoryActors;
    private Stage _stage;
    private InventoryState _inventoryState;

    private Image _characterSprite;

    //armor / weapon slots
    private InventorySlot _armorSlot;
    private InventorySlot _weaponSlot;

    private Label _DPValLabel;
    private int _DPVal = 0;
    private Label _APValLabel;
    private int _APVal = 0;

    private static float _slotWidth = 0;
    private static float _slotHeight = 0;

    private Array<InventoryObserver> _observers;

    private InventorySlotTooltip _inventorySlotTooltip;

    public InventoryUI(){
        super("Inventory", Utility.STATUSUI_SKIN, "solidbackground");

        _stage = new Stage();
        _inventoryState = new InventoryState();
        _characterSprite = new Image();

        _slotWidth = _stage.getWidth() / 12;
        _slotHeight = _stage.getWidth() / 12;

        _observers = new Array<InventoryObserver>();

        _dragAndDrop = new DragAndDrop();
        _inventoryActors = new Array<Actor>();

        //create
        _inventorySlotTable = new Table();
        _inventorySlotTable.setName("Inventory_Slot_Table");

        _playerSlotsTable = new Table();
        _equipSlots = new Table();
        _equipSlots.setName("Equipment_Slot_Table");

        _equipSlots.defaults().space(10);
        _inventorySlotTooltip = new InventorySlotTooltip(Utility.STATUSUI_SKIN);

        //back button
        TextButton backButton = new TextButton("Back", Utility.STATUSUI_SKIN);
        backButton.getLabel().setFontScale(3);

        Label DPLabel = new Label("Defense: ", Utility.STATUSUI_SKIN);
        _DPValLabel = new Label(String.valueOf(_DPVal), Utility.STATUSUI_SKIN);
        DPLabel.setFontScale(3);
        _DPValLabel.setFontScale(3);

        Label APLabel = new Label("Attack : ", Utility.STATUSUI_SKIN);
        _APValLabel = new Label(String.valueOf(_APVal), Utility.STATUSUI_SKIN);
        APLabel.setFontScale(3);
        _APValLabel.setFontScale(3);

        Table labelTable = new Table();
        labelTable.setDebug(true);
        labelTable.add(DPLabel).align(Align.left);
        labelTable.add(_DPValLabel).align(Align.center);
        labelTable.row();
        labelTable.row();
        labelTable.add(APLabel).align(Align.left);
        labelTable.add(_APValLabel).align(Align.center);

        InventorySlot headSlot = new InventorySlot(
                ItemUseType.ARMOR_HELMET.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_helmet")));

        _weaponSlot = new InventorySlot(
                ItemUseType.WEAPON_ONEHAND.getValue() |
                ItemUseType.WEAPON_TWOHAND.getValue() |
                ItemUseType.ARMOR_SHIELD.getValue() |
                ItemUseType.WAND_ONEHAND.getValue() |
                ItemUseType.WAND_TWOHAND.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_weapon"))
        );

        Gdx.app.log(TAG, "Weapon slot created");

        InventorySlot rightArmSlot = new InventorySlot(
                ItemUseType.WEAPON_ONEHAND.getValue() |
                ItemUseType.WEAPON_TWOHAND.getValue() |
                ItemUseType.ARMOR_SHIELD.getValue() |
                ItemUseType.WAND_ONEHAND.getValue() |
                ItemUseType.WAND_TWOHAND.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_shield"))
        );

        _armorSlot = new InventorySlot(
                ItemUseType.ARMOR_CHEST.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_chest")));

        InventorySlot legsSlot = new InventorySlot(
                ItemUseType.ARMOR_FEET.getValue(),
                new Image(Utility.ITEMS_TEXTUREATLAS.findRegion("inv_boot")));

        headSlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));
        _weaponSlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));
        rightArmSlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));
        _armorSlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));
        legsSlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));

        headSlot.addObserver(this);
        _weaponSlot.addObserver(this);
        rightArmSlot.addObserver(this);
        _armorSlot.addObserver(this);
        legsSlot.addObserver(this);

        _dragAndDrop.addTarget(new InventorySlotTarget(headSlot));
        _dragAndDrop.addTarget(new InventorySlotTarget(_weaponSlot));
        _dragAndDrop.addTarget(new InventorySlotTarget(_armorSlot));
        _dragAndDrop.addTarget(new InventorySlotTarget(rightArmSlot));
        _dragAndDrop.addTarget(new InventorySlotTarget(legsSlot));

        _playerSlotsTable.setBackground(new Image(new NinePatch(Utility.STATUSUI_TEXTUREATLAS.createPatch("dialog"))).getDrawable());

        _playerSlotsTable.setDebug(true);
        _inventorySlotTable.setDebug(true);
        _equipSlots.setDebug(true);

        //layout
        for(int i = 1; i <= _numSlots; i++){
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.addListener(new InventorySlotTooltipListener(_inventorySlotTooltip));
            _dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));

            _inventorySlotTable.add(inventorySlot).size(_slotWidth, _slotHeight);

            //inventorySlot.getTopInventoryItem().getDrawable().setMinWidth(_slotWidth);
            //inventorySlot.getTopInventoryItem().getDrawable().setMinHeight(_slotHeight);

            inventorySlot.addListener(new ClickListener() {
                                         @Override
                                         public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                                             super.touchUp(event, x, y, pointer, button);
                                             Gdx.app.log(TAG, "Inventory slot tapped");
                                             if( getTapCount() == 2 ){
                                                 InventorySlot slot = (InventorySlot)event.getListenerActor();
                                                 if( slot.hasItem() ){
                                                     InventoryItem item = slot.getTopInventoryItem();
                                                     if( item.isConsumable() ){
                                                         String itemInfo = item.getItemUseType() + Component.MESSAGE_TOKEN + item.getItemUseTypeValue();
                                                         InventoryUI.this.notify(itemInfo, InventoryObserver.InventoryEvent.ITEM_CONSUMED);
                                                         slot.removeActor(item);
                                                         slot.remove(item);
                                                     }
                                                 }
                                             }
                                         }


                                      }
            );

            if(i % _lengthSlotRow == 0){
                _inventorySlotTable.row();
            }
        }

        _equipSlots.add();
        _equipSlots.add(headSlot).size(_slotWidth, _slotHeight);
        _equipSlots.row();

        _equipSlots.add(_weaponSlot).size(_slotWidth, _slotHeight);
        _equipSlots.add(_armorSlot).size(_slotWidth, _slotHeight);
        _equipSlots.add(rightArmSlot).size(_slotWidth, _slotHeight);
        _equipSlots.row();

        _equipSlots.add();
        _equipSlots.right().add(legsSlot).size(_slotWidth, _slotHeight);

        _playerSlotsTable.add(_equipSlots);
        _inventoryActors.add(_inventorySlotTooltip);

        this.setDebug(true);
        this.add(backButton).top().left().align(Align.topLeft).width(_slotWidth * 2).height(_slotHeight);
        this.add(_characterSprite).height(_stage.getHeight() / 2).width(_stage.getHeight() / 2);
        this.add(_playerSlotsTable).padBottom(20);
        this.row();
        this.add(_inventorySlotTable).width(_stage.getWidth()).height(_stage.getHeight() / 2).colspan(5);
        this.row();
        this.pack();

        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                _inventoryState.closeInventory();
            }

        });
    }

    public InventorySlot getArmorSlot() { return _armorSlot; }
    public InventorySlot getWeaponSlot() { return _weaponSlot; }

    public void setCharacterSprite(Image sprite) {
        _characterSprite.setDrawable(sprite.getDrawable());
    }

    public InventoryState getCurrentState() { return _inventoryState; }

    public DragAndDrop getDragAndDrop(){
        return _dragAndDrop;
    }

    public Table getInventorySlotTable() {
        return _inventorySlotTable;
    }

    public Table getEquipSlotTable() {
        return _equipSlots;
    }

    public void resetEquipSlots(){
        _DPVal = 0;
        _APVal = 0;

        _DPValLabel.setText(String.valueOf(_DPVal));
        notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);

        _APValLabel.setText(String.valueOf(_APVal));
        notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);
    }

    public static void clearInventoryItems(Table targetTable){
        Array<Cell> cells = targetTable.getCells();
        for( int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot = (InventorySlot)cells.get(i).getActor();
            if( inventorySlot == null ) continue;
            inventorySlot.clearAllInventoryItems(false);
        }
    }

    public static Array<InventoryItemLocation> removeInventoryItems(String name, Table inventoryTable){
        Array<Cell> cells = inventoryTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            inventorySlot.removeAllInventoryItemsWithName(name);
        }
        return items;
    }

    public static void populateInventory(Table targetTable, Array<InventoryItemLocation> inventoryItems, DragAndDrop draganddrop, String defaultName, boolean disableNonDefaultItems){
        clearInventoryItems(targetTable);

        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < inventoryItems.size; i++){
            InventoryItemLocation itemLocation = inventoryItems.get(i);
            ItemTypeID itemTypeID = ItemTypeID.valueOf(itemLocation.getItemTypeAtLocation());
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(itemLocation.getLocationIndex()).getActor());

            for( int index = 0; index < itemLocation.getNumberItemsAtLocation(); index++ ){
                InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeID);
                Gdx.app.log(TAG, "Adding " + item.getItemTypeID() + " to inventory");
                String itemName =  itemLocation.getItemNameProperty();
                if( itemName == null || itemName.isEmpty() ){
                    item.setName(defaultName);
                }else{
                    item.setName(itemName);
                }

                item.getDrawable().setMinHeight(_slotHeight);
                item.getDrawable().setMinWidth(_slotWidth);

                inventorySlot.add(item);

                if( item.getName().equalsIgnoreCase(defaultName) ){
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                }else if( disableNonDefaultItems == false ){
                    draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
                }
            }


        }
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems();
            if( numItems > 0 ){
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table targetTable, String filterOutName){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems();
            if( numItems > 0 ){
                String topItemName = inventorySlot.getTopInventoryItem().getName();
                if( topItemName.equalsIgnoreCase(filterOutName)) continue;
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        inventorySlot.getTopInventoryItem().getName()));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventory(Table targetTable, String name){
        Array<Cell> cells = targetTable.getCells();
        Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            int numItems = inventorySlot.getNumItems(name);
            if( numItems > 0 ){
                //System.out.println("[i] " + i + " itemtype: " + inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems " + numItems);
                items.add(new InventoryItemLocation(
                        i,
                        inventorySlot.getTopInventoryItem().getItemTypeID().toString(),
                        numItems,
                        name));
            }
        }
        return items;
    }

    public static Array<InventoryItemLocation> getInventoryFiltered(Table sourceTable, Table targetTable, String filterOutName){
        Array<InventoryItemLocation> items = getInventoryFiltered(targetTable, filterOutName);
        Array<Cell> sourceCells = sourceTable.getCells();
        int index = 0;
        for( InventoryItemLocation item : items ) {
            for (; index < sourceCells.size; index++) {
                InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
                if (inventorySlot == null) continue;
                int numItems = inventorySlot.getNumItems();
                if (numItems == 0) {
                    item.setLocationIndex(index);
                    //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + numItems);
                    index++;
                    break;
                }
            }
            if( index == sourceCells.size ){
                //System.out.println("[index] " + index + " itemtype: " + item.getItemTypeAtLocation() + " numItems " + item.getNumberItemsAtLocation());
                item.setLocationIndex(index-1);
            }
        }
        return items;
    }

    public static void setInventoryItemNames(Table targetTable, String name){
        Array<Cell> cells = targetTable.getCells();
        for(int i = 0; i < cells.size; i++){
            InventorySlot inventorySlot =  ((InventorySlot)cells.get(i).getActor());
            if( inventorySlot == null ) continue;
            inventorySlot.updateAllInventoryItemNames(name);
        }
    }

    public boolean doesInventoryHaveSpace(){
        Array<Cell> sourceCells = _inventorySlotTable.getCells();
        int index = 0;

        for (; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if (inventorySlot == null) continue;
            int numItems = inventorySlot.getNumItems();
            if (numItems == 0) {
                return true;
            }else{
                index++;
            }
        }
        return false;
    }

    public void addEntityToInventory(Entity entity, String itemName){
        Array<Cell> sourceCells = _inventorySlotTable.getCells();

        for (int index = 0; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if (inventorySlot == null) continue;
            int numItems = inventorySlot.getNumItems();
            if (numItems == 0) {
                InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(ItemTypeID.valueOf(entity.getEntityConfig().getItemTypeID()));
                inventoryItem.setName(itemName);
                inventorySlot.add(inventoryItem);
                _dragAndDrop.addSource(new InventorySlotSource(inventorySlot, _dragAndDrop));
                break;
            }
        }
    }

    public void removeQuestItemFromInventory(String questID){
        Array<Cell> sourceCells = _inventorySlotTable.getCells();
        for (int index = 0; index < sourceCells.size; index++) {
            InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
            if (inventorySlot == null) continue;
            InventoryItem item = inventorySlot.getTopInventoryItem();
            if( item == null ) continue;
            String inventoryItemName = item.getName();
            if (inventoryItemName != null && inventoryItemName.equals(questID) ) {
                inventorySlot.clearAllInventoryItems(false);
            }
        }
    }

    public Array<Actor> getInventoryActors(){
        return _inventoryActors;
    }

    @Override
    public void onNotify(InventorySlot slot, SlotEvent event) {
        switch(event)
        {
            case ADDED_ITEM:
                InventoryItem addItem = slot.getTopInventoryItem();
                if( addItem == null ) return;
                if( addItem.isInventoryItemOffensive() ){
                    _APVal += addItem.getItemUseTypeValue();
                    _APValLabel.setText(String.valueOf(_APVal));
                    notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    if( addItem.isInventoryItemOffensiveWand() ){
                        notify(String.valueOf(addItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.ADD_WAND_AP);
                    }

                }else if( addItem.isInventoryItemDefensive() ){
                    _DPVal += addItem.getItemUseTypeValue();
                    _DPValLabel.setText(String.valueOf(_DPVal));
                    notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);
                }
                break;
            case REMOVED_ITEM:
                InventoryItem removeItem = slot.getTopInventoryItem();
                if( removeItem == null ) return;
                if( removeItem.isInventoryItemOffensive() ){
                    _APVal -= removeItem.getItemUseTypeValue();
                    _APValLabel.setText(String.valueOf(_APVal));
                    notify(String.valueOf(_APVal), InventoryObserver.InventoryEvent.UPDATED_AP);

                    if( removeItem.isInventoryItemOffensiveWand() ){
                        notify(String.valueOf(removeItem.getItemUseTypeValue()), InventoryObserver.InventoryEvent.REMOVE_WAND_AP);
                    }

                }else if( removeItem.isInventoryItemDefensive() ){
                    _DPVal -= removeItem.getItemUseTypeValue();
                    _DPValLabel.setText(String.valueOf(_DPVal));
                    notify(String.valueOf(_DPVal), InventoryObserver.InventoryEvent.UPDATED_DP);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addObserver(InventoryObserver inventoryObserver) {
        _observers.add(inventoryObserver);
    }

    @Override
    public void removeObserver(InventoryObserver inventoryObserver) {
        _observers.removeValue(inventoryObserver, true);
    }

    @Override
    public void removeAllObservers() {
        for(InventoryObserver observer: _observers){
            _observers.removeValue(observer, true);
        }
    }

    @Override
    public void notify(String value, InventoryObserver.InventoryEvent event) {
        for(InventoryObserver observer: _observers){
            observer.onNotify(value, event);
        }
    }
}
