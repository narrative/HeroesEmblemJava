package bunzosteele.heroesemblem.model.Units.Abilities;

import java.util.HashSet;
import java.util.List;

import bunzosteele.heroesemblem.model.BattleState;
import bunzosteele.heroesemblem.model.Battlefield.Tile;
import bunzosteele.heroesemblem.model.Units.Unit;

import com.badlogic.gdx.graphics.Color;

public abstract class Ability {
	
	public String displayName;
	public boolean isDaily;
	public boolean isActive;
	public boolean isTargeted;
	public Color abilityColor;
	public boolean exhausted;
	public boolean isMultiInput;
	public Unit target = null;
	
	public boolean CanUse(BattleState state, Unit originUnit){
		return false;
	}
	
	public HashSet<Tile> GetTargetTiles(BattleState state, Unit originUnit){
		return null;
	}
	
	public boolean Execute(BattleState state, Tile targetTile){
		return false;
	}
	
	public int AttackModifier(Unit attacker){
		return 0;
	}
	
	public boolean IsBlockingDamage(){
		return false;
	}
}