package edu.stuy;

import edu.stuy.subsystems.*;
import edu.stuy.util.Gamepad;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Wingman extends IterativeRobot {

    Shooter shooter;
    Drivetrain drivetrain;
    Acquirer acquirer;
    //CV cv;

    Gamepad rightPad = new Gamepad(Constants.GAMEPAD_RIGHT_PORT);
    Gamepad leftPad = new Gamepad(Constants.GAMEPAD_LEFT_PORT);

    SendableChooser autonChooser;

    public void robotInit() {
        shooter = Shooter.getInstance();
        drivetrain = Drivetrain.getInstance();
        acquirer = Acquirer.getInstance();
        //cv = CV.getInstance();
        resetAll();

        // SendableChooser for auton
        autonChooser = new SendableChooser();
        autonChooser.addDefault("1 - Wait for hot goal, shoot, and drive forward", Integer.valueOf(1));
        autonChooser.addObject("2 - Wait for hot goal, shoot first ball, intake second ball, shoot second ball, drive forward", Integer.valueOf(2));
        autonChooser.addObject("3 - Wait for hot goal (analog), shoot, and drive forward", Integer.valueOf(3));
        autonChooser.addObject("4 - Wait for hot goal (analog), shoot first ball, intake second ball, shoot second ball, drive forward", Integer.valueOf(4));
        autonChooser.addObject("5 - Shoot ball, drive forward", Integer.valueOf(5));
        autonChooser.addObject("7 - Shoot one ball without CV, drive forward", Integer.valueOf(6));
        autonChooser.addObject("7 - Shoot two balls without CV, drive forward", Integer.valueOf(7));
        autonChooser.addObject("8 - Drive forward, fire into low goal", Integer.valueOf(8));
        autonChooser.addObject("0 - Do nothing", Integer.valueOf(0));
        SmartDashboard.putData("Autonomous routine", autonChooser);
    }

    public void autonomousInit() {
        System.out.println("Initting auton");
        resetAll();
        Thread startRetractingWinch = new Thread(new Runnable() {

            public void run() {
                while (isAutonomous() && isEnabled()) {
                    shooter.retractWinch();
                }
            }

        });
        startRetractingWinch.start();
        Integer selection = (Integer) autonChooser.getSelected();
        System.out.println(selection.intValue());
        Autonomous.auton(selection.intValue());
    }

    // This function is called periodically during autonomous
    public void autonomousPeriodic() {
        //SmartDashboard.putBoolean("Goal hot?",cv.isGoalHot());
    }

    public void teleopInit() {
        resetAll();
    }

    public void disabledInit() {
    }

    // This function is called periodically during operator control
    public void teleopPeriodic() {
        SmartDashboard.putBoolean("Shooting?", shooter.isFullyRetracted());
        //SmartDashboard.putBoolean("Pi connected?", cv.isPiConnected());
        //SmartDashboard.putBoolean("CV - Goal Hot?", cv.isGoalHot());
        SmartDashboard.putNumber("Left Encoder Distance", drivetrain.getLeftEnc());
        SmartDashboard.putNumber("Right Encoder Distance", drivetrain.getRightEnc());
        SmartDashboard.putBoolean("Ready to shoot?", shooter.isFullyRetracted());
        //SmartDashboard.putBoolean("Ball Centered?", shooter.isBallCentered());
        //SmartDashboard.putBoolean("Hopper - Has Ball?", shooter.hasBall());
        //SmartDashboard.putBoolean("Shooter - Goal Hot?", shooter.isGoalHot());
        //SmartDashboard.putBoolean("Camera Light - On?", cv.getLightValue());
        acquirer.manualGamepadControl(leftPad);
        shooter.manualGamepadControl(leftPad);
        drivetrain.tankDrive(rightPad);
        //cv.setCameraLight(true);
    }

    // This function is called periodically during test mode
    public void testPeriodic() {
        // To be added later    
    }

    public void resetAll() {
        shooter.reset();
        drivetrain.reset();
        acquirer.reset();
        // Note: CV does not have a reset() method
    }
}
