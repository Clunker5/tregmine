package info.tregmine.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import info.tregmine.Tregmine;

public class Nickname {
	
	//Base values
	private Tregmine plugin;
	private TregminePlayer owner;
	
	//Nickname
	private String nickname;
	private String colorString;
	private ChatColor color = ChatColor.WHITE;
	private boolean hasColor = false;
	
	//Special nickname characteristics
	private ChatColor formatting;
	private boolean hasFormatting;
	private boolean showNickFlag;
	
	//Internal
	private String spacer = "$";
	private String breakcolor = ";;";
	
	public Nickname(Tregmine tregmine, TregminePlayer player, String setnickname){
		this.plugin = tregmine;
		this.owner = player;
		if(this.owner.getRank().canHaveHiddenNickname()){
			this.showNickFlag = false;
		}else{
			this.showNickFlag = true;
		}
		this.nickname = setnickname;
	}
	
	public void setNickname(String nick){
		this.nickname = nick;
	}
	
	public void setNickname(String nick, ChatColor setcolor){
		this.hasColor = true;
		this.nickname = nick;
		this.color = setcolor;
	}
	
	public void setNicknameColor(ChatColor setcolor){
		this.hasColor = true;
		this.color = setcolor;
		this.colorString = setcolor.name();
	}
	
	public String sqlColors(ChatColor color, ChatColor formatting){
		String colorname = color.getChar() + "";
		String formatname = formatting.name();
		return colorname + this.spacer + formatname + this.spacer + this.breakcolor;
	}
	
	public Map<ColorType, ChatColor> translateSql(String sql){
		Map<ColorType, ChatColor> map = new ArrayList<>();
		List<String> returnme = Arrays.asList(sql.split(spacer));
		ChatColor returnc;
		ChatColor returnf;
		for(String str : returnme){
			if(str.contains(this.breakcolor)){
				returnme.remove(str);
			}
			if(str.contains(this.spacer)){
				returnme.remove(str);
				returnme.add(str.replace(this.spacer, ""));
			}
		}
		for(String str : returnme)
		{
			if(ChatColor.getByChar(str).isColor()){
				returnc = ChatColor.getByChar(str);
				map.put(ColorType.COLOR, returnc);
			}else if(ChatColor.getByChar(str).isFormat()){
				returnf = ChatColor.getByChar(str);
			}else{
				continue;
			}
		}
	}
	
	public String getColorName(){
		if(this.hasColor){
		return this.colorString;
		}else{
			
		}
	}
	
	public void removeColor(){
		this.hasColor = false;
		this.color = ChatColor.WHITE;
	}
	
	public TregminePlayer getOwner(){
		return this.owner;
	}
	
	public String getNickname(){
		if(this.showNickFlag){
			if(hasColor && hasFormatting) return "!" + this.color + "" + this.formatting + this.nickname;
			else if(hasColor && !hasFormatting) return "!" + this.color +  "" + this.nickname;
			else return "!" + this.nickname;
		}else{
			if(hasColor && hasFormatting) return this.color + "" + this.formatting + this.nickname;
			else if(hasColor && !hasFormatting) return this.color +  "" + this.nickname;
			else return this.nickname;
		}
	}
	
	public ChatColor getChatColor(){
		if(hasColor) return this.color;
		else return ChatColor.WHITE;
	}
	
	public ChatColor getChatFormatting(){
		if(hasFormatting) return this.formatting;
		else return null;
	}
	
	public void setChatFormatting(ChatColor format){
		this.formatting = format;
		this.hasFormatting = true;
	}
	
	public void removeFormatting(){
		this.hasFormatting = false;
		this.formatting = ChatColor.WHITE;
	}
	
	public boolean hasChatColor(){
		return this.hasColor;
	}
	
	public void setOwner(TregminePlayer newowner){
		this.owner = newowner;
	}
	
	
}
