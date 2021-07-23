package game;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static game.InOutUtils.readStringsFromInputStream;
import static game.ProcessUtils.UTF_8;

/**
 * Main samplegame class.
 */
public class Main {

    public static void main(String[] args) {
        List<String> input = readStringsFromInputStream(System.in, UTF_8);
        if (!input.isEmpty()) {
            Round round = new Round(input);
            printMovingGroups(makeMove(round));
        }
        System.exit(0);
    }

    private static List<MovingGroup> makeMove(Round round) {
        List<MovingGroup> movingGroups = new ArrayList<>();


        if (round.getCurrentStep() == 0) {
            Integer id1 = round.getOwnPlanets().get(0).getId();
            round.getPlanets().stream().filter(planet -> round.getDistanceMap()[id1][planet.getId()] == 4).forEach(planet -> movingGroups.add(new MovingGroup(id1, planet.getId(), 15)));
            round.getPlanets().stream().filter(planet -> round.getDistanceMap()[id1][planet.getId()] == 8).forEach(planet -> movingGroups.add(new MovingGroup(id1, planet.getId(), 15)));
            round.getPlanets().stream().filter(planet -> planet.getId() == 5).forEach(planet -> movingGroups.add(new MovingGroup(id1, planet.getId(), 27)));
            round.getPlanets().stream().filter(planet -> planet.getId() == 4).forEach(planet -> movingGroups.add(new MovingGroup(id1, planet.getId(), 27)));
        } else {
            //защита
            Map<Integer, Integer> ownPlanet = new HashMap<Integer, Integer>();
            round.getOwnPlanets().forEach(
                    planet -> {
                        try {
                            ownPlanet.put(planet.getId(), planet.getPopulation());
                            List<MovingGroup> advMovingGroup = round.getAdversarysMovingGroups();
                            List<MovingGroup> toMyPlanetAdvMovGroupList = advMovingGroup.stream().filter(group -> planet.getId() == group.getTo()).collect(Collectors.toList());
                            List<MovingGroup> toMyPlanetOurMovGroupList = round.getOwnMovingGroups().stream().filter(group -> {
                                return planet.getId() == group.getTo();
                            }).collect(Collectors.toList());

                            toMyPlanetAdvMovGroupList.forEach(group ->
                                    ownPlanet.put(planet.getId(), (int) ownPlanet.get(planet.getId()) - group.getCount())
                            );
                            toMyPlanetOurMovGroupList.forEach(group ->
                                    ownPlanet.put(planet.getId(), (int) ownPlanet.get(planet.getId()) + group.getCount())
                            );
                        } catch (Exception e) {

                        }
                    }
            );

            ArrayList<Integer> idLos = new ArrayList<>();
            ArrayList<Integer> idWin = new ArrayList<>();
            ownPlanet.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue());
            for (Map.Entry<Integer, Integer> planet : ownPlanet.entrySet()) {
                if (planet.getValue() < 1) {
                    idLos.add(planet.getKey());
                } else {
                    idWin.add(planet.getKey());
                }
            }
            while (!idLos.isEmpty()) {
                if (!idWin.isEmpty()) {
                    movingGroups.add(new MovingGroup(idLos.get(0), idWin.get(0), (int) ((int) round.getPlanets().get(idWin.get(0)).getPopulation() * 0.2)));
                }

            }


        }


        return movingGroups;
    }

    private static void printMovingGroups(List<MovingGroup> moves) {
        System.out.println(moves.size());
        moves.forEach(move -> System.out.println(move.getFrom() + " " + move.getTo() + " " + move.getCount()));
    }

}
