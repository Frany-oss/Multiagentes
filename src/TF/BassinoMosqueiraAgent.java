package TF;

import agents.FieldAgent;
import classes.FieldUnity;
import environment.FoodSource;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static agents.FieldAgent.TYPE_MESSAGES.SOURCE;

public class BassinoMosqueiraAgent extends FieldAgent {

    private Timer timer;

    @Override
    protected void init() {
        super.init();

        if(timer == null) {
            timer = new Timer(2000, event -> {
                int count = 0;
                for(FieldUnity agent : getLocal().species.members.values()) {
                    if (agent.is_alive)
                        count++;
                }
                if (count <= 5 && getLocal().species.getStock() >= 50)
                    status = STATES.REPRODUCING;
            });
            timer.start();
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null)
            sendMessageAllAllies(SOURCE, source);
        FieldUnity enemy = detectEnemies();
        if (enemy != null)
            sendMessageAllAllies(ENEMY, enemy);
    }

    @Override
    protected void computeMessage(ACLMessage msg) {
        if (status == STATES.PATROLING) {

            if (msg.getContent().contains(ENEMY + "_")) {

                System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);

                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < 600) {
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            }
            else if (msg.getContent().contains(SOURCE + "_")){

                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);

                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
        }
    }
}
