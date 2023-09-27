package com.hbm.inventory.control_panel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;

public class SubElementItemChoice extends SubElement {

	public GuiButton pageLeft;
	public GuiButton pageRight;
	public int currentPage = 1;
	public int numPages = 1;
	public List<GuiButton> buttons = new ArrayList<>();
	
	public SubElementItemChoice(GuiControlEdit gui){
		super(gui);
	}
	
	@Override
	protected void initGui(){
		int cX = gui.width/2;
		int cY = gui.height/2;
		pageLeft = gui.addButton(new GuiButton(gui.currentButtonId(), cX-75, cY+92, 20, 20, "<"));
		pageRight = gui.addButton(new GuiButton(gui.currentButtonId(), cX+75, cY+92, 20, 20, ">"));
//		List<Control> controls = ControlRegistry.getAllControls();
//		for(int i = 0; i < controls.size(); i ++){
//			int offset = (i%7)*25;
//			buttons.add(gui.addButton(new GuiButton(i+1000, cX-70, cY-90 + offset, 160, 20, controls.get(i).name)));
//		}

		buttons.add(gui.addButton(new GuiButton(1000, cX-70, (cY-90) + (0%7)*25, 160, 20, "Button")));
		buttons.add(gui.addButton(new GuiButton(1001, cX-70, (cY-90) + (1%7)*25, 160, 20, "Switch")));
		buttons.add(gui.addButton(new GuiButton(1002, cX-70, (cY-90) + (2%7)*25, 160, 20, "Display")));
		buttons.add(gui.addButton(new GuiButton(1003, cX-70, (cY-90) + (3%7)*25, 160, 20, "Indicator")));

		numPages = (buttons.size()+6)/7;
		super.initGui();
	}
	
	@Override
	protected void drawScreen(){
		int cX = gui.width/2;
		int cY = gui.height/2;
		String text = currentPage + "/" + numPages;
		gui.getFontRenderer().drawString(text, cX, cY+100, 0xFF777777, false);
		text = "Select Control Type";
		gui.getFontRenderer().drawString(text, cX - gui.getFontRenderer().getStringWidth(text) / 2, cY-110, 0xFF777777, false);
	}
	
	private void recalculateVisibleButtons(){
		for(GuiButton b : buttons){
			b.visible = false;
			b.enabled = false;
		}
		int idx = (currentPage-1)*7;
		for(int i = idx; i < idx+7; i ++){
			if(i >= buttons.size())
				break;
			buttons.get(i).visible = true;
			buttons.get(i).enabled = true;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button == pageLeft){
			currentPage = Math.max(1, currentPage - 1);
			recalculateVisibleButtons();
		} else if(button == pageRight){
			currentPage = Math.min(numPages, currentPage + 1);
			recalculateVisibleButtons();
		} else {
			switch (button.id) {
				case 1000:
					gui.currentEditControl = ControlRegistry.getNew("button_push", gui.control.panel);
					gui.itemConfig.variants = ControlRegistry.getAllControlsOfType(gui.currentEditControl.getControlType());
					gui.pushElement(gui.itemConfig);
					break;
				case 1001:
					gui.currentEditControl = ControlRegistry.getNew("switch_toggle", gui.control.panel);
					gui.itemConfig.variants = ControlRegistry.getAllControlsOfType(gui.currentEditControl.getControlType());
					gui.pushElement(gui.itemConfig);
					break;
				case 1002:
					gui.currentEditControl = ControlRegistry.getNew("display_7seg", gui.control.panel);
					gui.itemConfig.variants = ControlRegistry.getAllControlsOfType(gui.currentEditControl.getControlType());
					gui.pushElement(gui.itemConfig);
					break;
				case 1003:
					gui.currentEditControl = ControlRegistry.getNew("indicator_lamp", gui.control.panel);
					gui.pushElement(gui.linker);
					break;
			}
		}
	}
	
	@Override
	protected void enableButtons(boolean enable) {
		if (enable) {
			recalculateVisibleButtons();
		} else {
			for (GuiButton b : buttons) {
				b.visible = false;
				b.enabled = false;
			}
		}
		pageLeft.visible = enable;
		pageLeft.enabled = enable;
		pageRight.visible = enable;
		pageRight.enabled = enable;
	}
}
