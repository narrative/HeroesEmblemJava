package bunzosteele.heroesemblem.view;

import java.util.HashSet;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import bunzosteele.heroesemblem.HeroesEmblem;
import bunzosteele.heroesemblem.model.BattleState;
import bunzosteele.heroesemblem.model.CombatHelper;
import bunzosteele.heroesemblem.model.MovementHelper;
import bunzosteele.heroesemblem.model.Battlefield.Tile;
import bunzosteele.heroesemblem.model.Units.Unit;

public class BattleWindow {
	HeroesEmblem game;
	BattleState state;
	Texture img;
	int idleFrame = 1;
	int attackFrame = 1;
	int xOffset;
	int yOffset;
	int width;
	int height;
	int tileWidth;
	int tileHeight;
	
	public BattleWindow(HeroesEmblem game, BattleState state, int width, int height, int yOffset){
		this.game = game;
		this.state = state;
		Timer.schedule(new Task(){
			@Override
			public void run(){
				idleFrame++;
				if(idleFrame > 3)
					idleFrame = 1;
			}}, 0 , 1/3f);
		xOffset = 0;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
		tileWidth = width / 16;
		tileHeight = height / 9;
	}
	
	public void draw(){
		drawBattlefield();
		drawUnits();
	}
	
	private void drawBattlefield(){
		int rowOffset = 1;
		for(List<Tile> row : state.battlefield){
			int tileOffset = 0;
			for(Tile tile : row){
				AtlasRegion tileRegion = game.textureAtlas.findRegion(tile.type.toString());
				Sprite tileSprite = new Sprite(tileRegion);
				game.batcher.draw(tileSprite, xOffset + tileWidth * tileOffset, Gdx.graphics.getHeight() - (tileHeight * rowOffset), tileWidth, tileHeight);
				tileOffset ++;
			}
			rowOffset++;
		}
	}
	
	public void drawHighlights(){
		if(state.isMoving){
			HashSet<Tile> options = MovementHelper.GetMovementOptions(state);
			Color color = new Color(.2f, .2f, .7f, .5f);
			for(Tile tile : options){
				drawHighlight(tile.x, tile.y, color);
			}
		}
		if(state.isAttacking){
			HashSet<Tile> options = CombatHelper.GetAttackOptions(state, state.selected);
			Color color = new Color(.7f, .2f, .2f, .5f);
			for(Tile tile : options){
				drawHighlight(tile.x, tile.y, color);
			}
		}
		if(state.isUsingAbility){
			HashSet<Tile> options = state.selected.ability.GetTargetTiles(state, state.selected);
			for(Tile tile : options){
				drawHighlight(tile.x, tile.y, state.selected.ability.abilityColor);
			}
		}
	}
	
	public void drawHealthBars(){
		for(Unit unit : state.AllUnits()){
			drawHealthBar(unit);
		}
	}
	
	private void drawHealthBar(Unit unit){
		float healthPercent = unit.currentHealth / (float) unit.maximumHealth;
		if (healthPercent > 0){
			Color color;
			if(healthPercent > .7){
				color = new Color(0f, 1f, 0f, 1f);
			}else if(healthPercent > .3){
				color = new Color(1f, 1f, 0f, 1f);
			}else{
				color = new Color(1f, 0f, 0f, 1f);
			}
		
			game.shapeRenderer.setColor(color);	
			game.shapeRenderer.rect((unit.x * tileWidth) + xOffset, Gdx.graphics.getHeight() - (tileHeight * (unit.y+1)), tileWidth * healthPercent, tileHeight/10);
		}
	}
	
	private void drawUnits(){
		for(Unit unit : state.AllUnits()){
			if(unit.isAttacking){
				UnitRenderer.DrawUnit(game, unit, unit.x * tileWidth, Gdx.graphics.getHeight() - (unit.y + 1) * tileHeight, tileWidth, "Attack", unit.attackFrame);
			}else{
				UnitRenderer.DrawUnit(game, unit, unit.x * tileWidth, Gdx.graphics.getHeight() - (unit.y + 1) * tileHeight, tileWidth, "Idle", idleFrame, state.IsTapped(unit));					
			}
		}
	}
	
	private void drawHighlight(int x, int y, Color color){
		game.shapeRenderer.setColor(color);	
		game.shapeRenderer.rect((x * tileWidth) + xOffset, Gdx.graphics.getHeight() - (tileHeight * (y+1)), tileWidth, tileHeight);
	}
	
	public boolean isTouched(float x, float y){
		if(x >= xOffset && x < xOffset + width){
			if( y >= yOffset && y < yOffset + height){
				return true;
			}
		}
		return false;
	}
	
	public void processTouch(float x, float y){		
		if(state.selected != null && state.selected.ability.isMultiInput && state.selected.ability.target != null){
			
		}
		
		
		if (state.selected != null && state.isAttacking){
			HashSet<Tile> options = CombatHelper.GetAttackOptions(state, state.selected);
			for(Tile tile : options){
				if(tile.x * tileWidth < x && x <= tile.x * tileWidth + tileWidth){
					if(Gdx.graphics.getHeight() - (tile.y + 1) * tileHeight < y && y <= Gdx.graphics.getHeight() - (tile.y) * tileHeight){
						for(Unit enemy : state.enemies){
							if(enemy.x == tile.x && enemy.y == tile.y){
								state.selected.startAttack();
								if(CombatHelper.Attack(state.selected, enemy, state.battlefield)){
									enemy.startDamage();
									if(enemy.currentHealth <= 0){
										state.selected.giveExperience(enemy.maximumHealth);
										enemy.startDeath();
									}	
								}else{
									enemy.startMiss();
								}

								state.isAttacking = false;
								state.selected.hasAttacked = true;
								state.selected = null;
								return;
							}
						}
					}
				}
			}
		}		
		
		if(state.selected != null && state.isUsingAbility){
			HashSet<Tile> options = state.selected.ability.GetTargetTiles(state, state.selected);
			for(Tile tile : options){
				if(tile.x * tileWidth < x && x <= tile.x * tileWidth + tileWidth){
					if(Gdx.graphics.getHeight() - (tile.y + 1) * tileHeight < y && y <= Gdx.graphics.getHeight() - (tile.y) * tileHeight){
						if(state.selected.ability.Execute(state, tile)){
							state.isUsingAbility = false;
							state.selected.hasAttacked = true;
							state.selected.ability.exhausted = true;
							for(Unit unit : state.AllUnits()){
								if(unit.currentHealth <= 0){
									state.selected.giveExperience(unit.maximumHealth);
									unit.startDeath();
								}
							}
							state.selected = null;
							return;
						}else{
							if(!state.selected.ability.isMultiInput || state.selected.ability == null){
								state.isUsingAbility = false;
								state.selected = null;
							}else{
								return;
							}
						}
					}
				}
			}
		}
		
		for(Unit unit : state.AllUnits()){
			if(unit.x * tileWidth < x && x <= unit.x * tileWidth +tileWidth){
				if(Gdx.graphics.getHeight() - (unit.y + 1) * tileHeight < y && y <= Gdx.graphics.getHeight() - (unit.y) * tileHeight){
					if(state.selected != null && state.selected.ability != null && state.selected.ability.target != null)
						state.selected.ability.target = null;
					state.selected = unit;
					state.isMoving = false;
					state.isAttacking = false;
					state.isUsingAbility = false;
					return;
				}
			}
		}
		
		if (state.selected != null && state.isMoving){
			HashSet<Tile> options = MovementHelper.GetMovementOptions(state);
			for(Tile tile : options){
				if(tile.x * tileWidth < x && x <= tile.x * tileWidth + tileWidth){
					if(Gdx.graphics.getHeight() - (tile.y + 1) * tileHeight < y && y <= Gdx.graphics.getHeight() - (tile.y) * tileHeight){
						state.selected.distanceMoved = (Math.abs(state.selected.x - tile.x) + Math.abs(state.selected.y - tile.y));
						state.selected.x = tile.x;
						state.selected.y = tile.y;
						state.isMoving = false;
						state.selected.hasMoved = true;
						return;
					}
				}
			}
		}
		
		if(state.selected != null && state.selected.ability != null && state.selected.ability.target != null)
			state.selected.ability.target = null;
		state.selected = null;
		state.isAttacking = false;
		state.isMoving = false;
		state.isUsingAbility = false;
	}
}