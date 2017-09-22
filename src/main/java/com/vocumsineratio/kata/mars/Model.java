/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Model {
    static Output runSimulation(Input input) {
        SimulationDefinition simulationDefinition = Builder.buildSimulation(input);

        SimulationDefinition simulationResults = runSimulation(simulationDefinition);

        final Output output = prepareReport(simulationResults);
        return output;
    }

    private static Output prepareReport(SimulationDefinition simulationResults) {
        return prepareReport(projectResults(simulationResults));
    }

    private static Output prepareReport(List<RoverState> simulationResults) {
        List<Output.Rover> rovers = new ArrayList<>(simulationResults.size());
        for (RoverState state : simulationResults) {
            SimpleHeading source = state.orientation;

            // TODO: EnumMap?
            Output.Heading heading = Output.Heading.valueOf(source.name());
            Output.Coordinate coordinate = new Output.Coordinate(state.posX, state.posY);
            Output.Rover rover = new Output.Rover(coordinate, heading);
            rovers.add(rover);

        }
        return new Output(rovers);
    }

    private static List<RoverState> projectResults(SimulationDefinition simulationResult) {
        List<RoverState> out = new ArrayList<>();
        final Iterable<Domain.Rover<RoverState>> entries = simulationResult.entries();
        for(Domain.Rover<RoverState> entry : entries) {
            out.add(entry.position());
        }
        return out;
    }

    private static SimulationDefinition runSimulation(SimulationDefinition simulationDefinition) {
        GridDefinition gridDefinition = simulationDefinition.grid;
        ArrayGrid grid = ArrayGrid.from(gridDefinition.maxRight, gridDefinition.maxUp);

        ReportBuilder reportBuilder = new ReportBuilder(simulationDefinition.grid);

        reportBuilder = Domain.runSimulation(grid, simulationDefinition, reportBuilder);

        return reportBuilder.build();
    }

    private static final class ReportBuilder implements Domain.Report<ReportBuilder, RoverState>{
        private final SimulationDefinition definition;

        ReportBuilder(GridDefinition grid) {
            this(new SimulationDefinition(grid, Collections.EMPTY_LIST));
        }

        ReportBuilder(SimulationDefinition current) {
            this.definition = current;
        }

        public ReportBuilder add(RoverState r) {
            ArrayList<Domain.Rover<RoverState>> rovers = new ArrayList<>();

            for (Domain.Rover<RoverState> entry : definition.entries()) {
                rovers.add(entry);
            }

            rovers.add(new RoverDefinition(r, Collections.EMPTY_LIST));

            SimulationDefinition next = new SimulationDefinition(definition.grid, rovers);
            return new ReportBuilder(next);
        }

        SimulationDefinition build() {
            return definition;
        }
    }

    private static final class ArrayGrid implements Domain.Plateau<ArrayGrid, RoverState>, Domain.PlateauView<RoverState> {

        private final boolean[][] positions;

        ArrayGrid(boolean[][] positions) {
            this.positions = positions;
        }

        private ArrayGrid roverArrived(int posX, int posY) {
            boolean [][]clone = cloneState();
            clone[posX][posY] = true;
            return new ArrayGrid(clone);
        }

        private ArrayGrid roverLeft(int posX, int posY) {
            boolean [][]clone = cloneState();
            clone[posX][posY] = false;
            return new ArrayGrid(clone);
        }

        private boolean isOccupied(int posX, int posY) {
            return positions[posX][posY];
        }

        private boolean [][] cloneState() {
            int maxX = positions.length;
            int maxY = positions[0].length;

            boolean [][] clone = new boolean[maxX][maxY];

            for (int posX = 0; posX < maxX; ++posX) {
                for(int posY = 0; posY < maxY; ++posY) {
                    clone[posX][posY] = positions[posX][posY];
                }
            }

            return clone;
        }
        static ArrayGrid from(int maxRight, int maxUp) {
            boolean[][] positions = new boolean[1 + maxRight][1 + maxUp];
            return new ArrayGrid(positions);
        }

        @Override
        public ArrayGrid roverArrived(RoverState rover) {
            return roverArrived(rover.posX, rover.posY);
        }

        @Override
        public ArrayGrid roverLeft(RoverState rover) {
            return roverLeft(rover.posX, rover.posY);
        }

        @Override
        public boolean isOccupied(RoverState rover) {
            return isOccupied(rover.posX, rover.posY);
        }
    }

    private static class Move {
        final int offsetX;
        final int offsetY;

        Move(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    private enum SimpleHeading implements Domain.Heading<SimpleHeading> {
        N {
            @Override
            public SimpleHeading left() {
                return W;
            }

            @Override
            public SimpleHeading right() {
                return E;
            }
        },
        E {
            @Override
            public SimpleHeading left() {
                return N;
            }

            @Override
            public SimpleHeading right() {
                return S;
            }
        },
        W {
            @Override
            public SimpleHeading left() {
                return S;
            }

            @Override
            public SimpleHeading right() {
                return N;
            }
        },
        S {
            @Override
            public SimpleHeading left() {
                return E;
            }

            @Override
            public SimpleHeading right() {
                return W;
            }
        };

    }

    private static final class RoverState implements Domain.Position<RoverState> {
        final int posX;
        final int posY;
        final SimpleHeading orientation;

        RoverState(int posX, int posY, SimpleHeading orientation) {
            this.posX = posX;
            this.posY = posY;
            this.orientation = orientation;
        }

        @Override
        public RoverState left() {
            return new RoverState(posX, posY, orientation.left());
        }

        @Override
        public RoverState right() {
            return new RoverState(posX, posY, orientation.right());
        }

        @Override
        public RoverState move() {
            final Map<SimpleHeading, Move> moves = new EnumMap<>(SimpleHeading.class);
            {
                moves.put(SimpleHeading.W, new Move(-1, 0));
                moves.put(SimpleHeading.E, new Move(1, 0));
                moves.put(SimpleHeading.N, new Move(0, 1));
                moves.put(SimpleHeading.S, new Move(0, -1));
            }

            Move move = moves.get(orientation);
            return new RoverState(posX + move.offsetX, posY + move.offsetY, orientation);
        }
    }

    private static class RoverDefinition implements Domain.Rover<RoverState> {
        public final RoverState state;
        public final List<Domain.Instruction<RoverState>> instructions;

        RoverDefinition(RoverState state, List<Domain.Instruction<RoverState>> instructions) {
            this.state = state;
            this.instructions = instructions;
        }

        @Override
        public RoverState position() {
            return state;
        }

        @Override
        public Iterable<Domain.Instruction<RoverState>> program() {
            return instructions;
        }
    }

    private static class GridDefinition {
        public final int maxRight;
        public final int maxUp;

        public GridDefinition(int maxRight, int maxUp) {
            this.maxRight = maxRight;
            this.maxUp = maxUp;
        }
    }

    private static class SimulationDefinition implements Domain.Simulation<RoverState, ArrayGrid> {
        public final GridDefinition grid;
        public final Iterable<Domain.Rover<RoverState>> rovers;

        public SimulationDefinition(GridDefinition grid, Iterable<Domain.Rover<RoverState>> rovers) {
            this.grid = grid;
            this.rovers = rovers;
        }

        @Override
        public ArrayGrid plateau() {
            return ArrayGrid.from(grid.maxRight, grid.maxUp);
        }

        @Override
        public Iterable<Domain.Rover<RoverState>> entries() {
            return rovers;
        }
    }


    private static class Builder {
        private static RoverState buildRoverState(Input.Position position) {
            // TODO: this is probably an enum map
            SimpleHeading heading = SimpleHeading.valueOf(position.heading.name());
            return new RoverState(position.coordinate.X, position.coordinate.Y, heading);
        }

        private static RoverDefinition buildRover(Input.Rover rover) {
            RoverState roverState = buildRoverState(rover.position);
            List<Domain.Instruction<RoverState>> program = buildProgram(rover.instructions);

            return new RoverDefinition(roverState, program);
        }

        private static List<Domain.Instruction<RoverState>> buildProgram(List<Input.Instruction> instructions) {


            Map<Input.Instruction, Domain.Instruction<RoverState>> instructionSet = new HashMap<>();
            
            instructionSet.put(Input.Instruction.M, rover -> rover.move());
            instructionSet.put(Input.Instruction.L, rover -> rover.left());
            instructionSet.put(Input.Instruction.R, rover -> rover.right());

            List<Domain.Instruction<RoverState>> program = new ArrayList<>();
            for (Input.Instruction instruction : instructions) {
                Domain.Instruction currentInstruction = instructionSet.get(instruction);
                program.add(currentInstruction);
            }
            return program;
        }

        private static SimulationDefinition buildSimulation(Input input) {
            List<Domain.Rover<RoverState>> rovers = new ArrayList<>();
            for (Input.Rover currentRover : input.rovers) {
                RoverDefinition roverDefinition = Builder.buildRover(currentRover);
                rovers.add(roverDefinition);
            }

            GridDefinition grid = new GridDefinition(input.plateau.upperRight.X, input.plateau.upperRight.Y);
            return new SimulationDefinition(grid, rovers);
        }
    }

}
