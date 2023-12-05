package screen;

import static org.junit.jupiter.api.Assertions.*;

import engine.DrawManager.SpriteType;
import engine.GameSettings;
import engine.GameState;
import entity.Bullet;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/** 테스트 클래스: GameScreenTest */
class GameScreenTest {

  /** 게임 화면의 너비 */
  private static final int WIDTH = 448;
  /** 게임 화면의 높이 */
  private static final int HEIGHT = 520;
  /** 초당 프레임 수 */
  private static final int FPS = 60;
  /** 최대 목숨 수 */
  private static final int MAX_LIVES = 3;
  /** 테스트 설정 : 실제 설정과 같은 설정 */
  private static final GameSettings SETTINGS_BASE_LEVEL =
      new GameSettings(5, 4, 60, 2000);
  /** 테스트 설정 : Ship이 빠르게 죽는 상황을 볼 수 있도록 설정 */
  private static final GameSettings SETTINGS_MANY_ENEMYSHIPS =
      new GameSettings(10, 10, 40, 10);
  /** 테스트 설정 : EnemyShip들이 빠르게 전멸하는 상황을 볼 수 있도록 설정 */
  private static final GameSettings SETTINGS_ONE_ENEMYSHIP =
      new GameSettings(1, 1, 30, 10000);
  /** 게임 상태 */
  private static GameState gameState;
  /** 게임 설정 리스트 */
  private static List<GameSettings> gameSettings;
  /** 테스트할 게임 화면 */
  private static GameScreen gameScreen;
  /** 현재 게임 설정 */
  private static GameSettings currentGameSettings;
  /** 현재 가상 스크린에 날아가고 있는 총알들*/
  private static Set<Bullet> bullets;
  /** 현재 가상 스크린에 생성된 EnemyShipFormation*/
  private static EnemyShipFormation enemyShipFormation;
  /** 현재 가상 스크린에 생성된 Ship*/
  private static Ship ship;
  /** 테스트 레벨*/
  private static int testLevel;
  /** 보너스 목숨 여부*/
  private static boolean bonusLife;
  /** manageCollisions 메소드 테스트1를 통과했는지 여부*/
  private static boolean testGameScreenManageCollisions1Pass;
  /** manageCollisions 메소드 테스트2를 통과했는지 여부*/
  private static boolean testGameScreenManageCollisions2Pass;

  /** 테스트 실행 전 설정 초기화*/
  @BeforeAll
  static void setUp() {
    gameSettings = new ArrayList<GameSettings>();
    gameSettings.add(SETTINGS_BASE_LEVEL);
    gameSettings.add(SETTINGS_MANY_ENEMYSHIPS);
    gameSettings.add(SETTINGS_ONE_ENEMYSHIP);
    gameSettings.add(SETTINGS_MANY_ENEMYSHIPS);
    gameSettings.add(SETTINGS_ONE_ENEMYSHIP);
    testLevel = 1;
    testGameScreenManageCollisions1Pass = false;
    testGameScreenManageCollisions2Pass = false;
  }

  /** 각 테스트 메소드 실행 전 GameScreen 초기화*/
  @BeforeEach
  void initialize() {
    bonusLife = false;
    gameState = new GameState(testLevel, 0, MAX_LIVES, 0, 0);
    currentGameSettings = gameSettings.get(gameState.getLevel() - 1);
    gameScreen = new GameScreen(gameState, currentGameSettings, bonusLife, WIDTH, HEIGHT, FPS);
    gameScreen.initialize();
    bullets = gameScreen.getBullets();
    enemyShipFormation = gameScreen.getEnemyShipFormation();
    ship = gameScreen.getShip();
    testLevel++;
  }

  @Test
  @DisplayName("initialize 메소드 테스트: GameScreen이 객체들을 잘 생성하는지 확인")
  void testGameScreenInitialization() {
    /** gameScreen initialize*/
    assertNotNull(gameScreen); // gameScreen이 null이면 안됨.
    assertNotNull(gameScreen.getShip()); // Ship 생성여부확인
    assertNotNull(gameScreen.getEnemyShipFormation()); // EnemyShipFormation 생성여부확인
    assertEquals(WIDTH / 2, gameScreen.getShip().getPositionX()); // Ship이 지정된 위치에 생성되었는지 확인
    assertEquals(HEIGHT - 30, gameScreen.getShip().getPositionY()); // Ship이 지정된 위치에 생성되었는지 확인
    assertFalse(gameScreen.getEnemyShipFormation().isEmpty()); // EnemyShipFormation이 비어있으면 안됨

    /** bonusLife == false인 상황*/
    assertEquals(MAX_LIVES, gameScreen.getLives()); // lives == MAX_LIVES

    /** bonusLife == true인 상황*/
    bonusLife = true;
    gameScreen = new GameScreen(gameState, currentGameSettings, bonusLife, WIDTH, HEIGHT, FPS);
    gameScreen.initialize();
    assertEquals(MAX_LIVES + 1, gameScreen.getLives()); // // lives == MAX_LIVES+1
  }

  @Test
  @DisplayName("manageCollisions 메소드 테스트1: Ship이 총알을 맞는 경우")
  void testGameScreenManageCollisions1() {
    /** gameScreen initialize*/
    assertEquals(MAX_LIVES, gameScreen.getLives()); // lives == MAX_LIVES

    /** Ship과 동일한 위치에 EnemyBullet 생성*/
    Bullet bullet = new Bullet(ship.getPositionX(), ship.getPositionY(), 1);
    bullets.add(bullet);
    gameScreen.manageCollisions(); // 총에 맞았는지 확인

    /** 총 맞은 후 */
    assertEquals(MAX_LIVES - 1, gameScreen.getLives()); // lives == MAX_LIVES-1
    testGameScreenManageCollisions1Pass = true;
  }


  @Test
  @DisplayName("manageCollisions 메소드 테스트2: EnemyShip이 총알을 맞는 경우")
  void testGameScreenManageCollisions2() {
    /** gameScreen initialize*/
    EnemyShip enemyShip = enemyShipFormation.iterator().next();
    assertFalse(enemyShip.isDestroyed()); // enemyShip의 isDestroyed 값이 False

    /** EnemyShip과 동일한 위치에 Bullet 생성*/
    Bullet bullet = new Bullet(enemyShip.getPositionX(), enemyShip.getPositionY(), -1);
    bullets.add(bullet);
    gameScreen.manageCollisions(); // 총에 맞았는지 확인

    /** 총 맞은 후 */
    assertTrue(enemyShip.isDestroyed()); // enemyShip의 isDestroyed 값이 True
    testGameScreenManageCollisions2Pass = true;
  }

  @Test
  @EnabledIf("testGameScreenManageCollisions1Pass")
  // testGameScreenManageCollisions1가 통과하지 못하면 테스트를 수행하지 않음
  @DisplayName("update 메소드 테스트1: Ship이 체력을 다 잃어서 게임이 끝나는 경우")
  void testGameScreenUpdate1() {
    /** gameScreen initialize*/
    assertFalse(gameScreen.isLevelFinished()); // 생성된 직후에는 LevelFinished 값이 False.
    assertEquals(SpriteType.Ship, ship.getSpriteType()); // 생성된 직후에는 Ship의 SpriteType은 Ship

    long loopStartTime = System.currentTimeMillis();
    long loopDuration = 15000; // 15초 (단위: 밀리초)

    /** 가상 게임 실행 (ship이 EnemyShip을 다 파괴할 때까지 반복 or 15초 이상 실행하면 강제 종료)*/
    while (gameScreen.getLives() != 0) {
      gameScreen.update();
      // 10초 이내에 루프를 빠져나오도록 확인
      if (System.currentTimeMillis() - loopStartTime > loopDuration) {
        fail("루프가 10초 이상 실행됨"); // 테스트 실패로 처리하거나 다른 적절한 조치를 취할 수 있습니다.
      }
    }
    ship.update();

    /** 가상 게임 종료*/
    assertTrue(gameScreen.isLevelFinished()); // 가상 게임 종료 후에는 LevelFinished 값이 True.
    assertEquals(SpriteType.ShipDestroyed,
        ship.getSpriteType()); // 가상 게임 종료 후에는 Ship의 SpriteType은 ShipDestroyed

  }

  @Test
  @EnabledIf("testGameScreenManageCollisions2Pass")
  // testGameScreenManageCollisions1가 통과하지 못하면 테스트를 수행하지 않음
  @DisplayName("update 메소드 테스트2: EnemyShip이 다 죽어서 게임이 끝나는 경우")
  void testGameScreenUpdate2() {
    /** gameScreen initialize*/
    EnemyShip enemyShip = enemyShipFormation.iterator().next();
    assertFalse(gameScreen.isLevelFinished()); // 생성된 직후에는 LevelFinished 값이 False.
    assertNotEquals(SpriteType.Explosion,
        enemyShip.getSpriteType()); // 생성된 직후에는 enemyShip의 SpriteType은 Explosion이 아니어야 함.

    long loopStartTime = System.currentTimeMillis();
    long loopDuration = 15000; // 15초 (단위: 밀리초)
    /** 가상 게임 실행 (ship이 EnemyShip을 다 파괴할 때까지 반복 or 15초 이상 실행하면 강제 종료)*/
    Set<Bullet> bullets = gameScreen.getBullets();
    while (!enemyShipFormation.isEmpty()) {
      ship.shoot(bullets);
      gameScreen.update();
      // 10초 이내에 루프를 빠져나오도록 확인
      if (System.currentTimeMillis() - loopStartTime > loopDuration) {
        fail("루프가 10초 이상 실행됨"); // 테스트 실패로 처리하거나 다른 적절한 조치를 취할 수 있습니다.
      }
    }

    /** 가상 게임 종료*/
    assertTrue(gameScreen.isLevelFinished()); // 가상 게임 종료 후에는 LevelFinished 값이 True.
    assertEquals(SpriteType.Explosion,
        enemyShip.getSpriteType()); // 가상 게임 종료 후에는 enemyShip의 SpriteType은 Explosion

  }

  boolean testGameScreenManageCollisions1Pass() {
    return testGameScreenManageCollisions1Pass;
  }

  boolean testGameScreenManageCollisions2Pass() {
    return testGameScreenManageCollisions2Pass;
  }

}
