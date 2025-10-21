
package org.firstinspires.ftc.teamcode.OpModes;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.LogFile;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Pose2dWrapper;
import org.firstinspires.ftc.teamcode.gamepad.InputAutoMapper;
import org.firstinspires.ftc.teamcode.gamepad.InputHandler;

@Config
@Autonomous(name = "MecanumAuto")
public class MecanumAuto extends LinearOpMode {


    ElapsedTime runtime = new ElapsedTime();
    boolean inputComplete = false;
    boolean side = true;
    boolean experimental = false;

    double delay;
    double delay1 = 1;

    double delay2 = 2;

    double delay3 = 3;

    public static boolean logHPZSide = false;
    public static boolean logGoalSide = false;
    public static boolean logDetails = false;
    public double waitForLaunch;
    public Pose2dWrapper startPose;
    public Pose2dWrapper firstLaunchPose;
    public Pose2dWrapper firstPickupPose;
    public Pose2dWrapper firstPickupFinishedPose;
    public Pose2dWrapper secondLaunchPose;
    public Pose2dWrapper secondPickupFinishedPose;
    public Pose2dWrapper thirdLaunchPose;
    public Pose2dWrapper thirdPickupFinishedPose;

    LogFile HPZSideLog;
    LogFile goalSideLog;
    LogFile detailsLog;

    /**
     * Runs though linear OpMode once, initializes, waits for user input, and performs a linear sequence
     * based on the initialization inputs. Designed for 2024 Into The Deep.
     *
     * @throws InterruptedException When giving a command to stop in the middle of a while loop, an exception is thrown
     */
    @Override
    public void runOpMode() throws InterruptedException {

        if (logHPZSide) {
            HPZSideLog = new LogFile(LogFile.FileType.Action, "HPZ", "csv");
        }

        if (logGoalSide) {
            goalSideLog = new LogFile(LogFile.FileType.Action, "Goal", "csv");
        }

        if (logDetails) {
            detailsLog = new LogFile(LogFile.FileType.Details, "details", "csv");
        }

        runtime.reset();


        while (!inputComplete) {
            if (gamepad1.xWasReleased()) {
                inputComplete = true;
            }
            if (gamepad1.yWasReleased()) {
                experimental = !experimental;
            }
            if (gamepad1.aWasReleased()) {
                side = !side;
            }
            if (gamepad1.dpadRightWasReleased())
            {
                delay = delay1;
            }
            if (gamepad1.dpadLeftWasReleased())
            {
                delay = delay2;
            }
            if (gamepad1.dpadUpWasReleased())
            {
                delay = delay3;
            }

            telemetry.addData("Compiled on:", BuildConfig.COMPILATION_DATE);
            telemetry.addData("-----Initialization-----", "");
            telemetry.addLine();
            telemetry.addData("Auto Mode: ", side ? "HPZ side Scoring" : "Goal side Scoring");
            telemetry.addData("Experimental Mode? ", experimental ? "Enabled" : "Disabled");
            telemetry.addData("Press X to finalize values", inputComplete);
            telemetry.addData("delay value", delay);
            telemetry.update();

        }
        if (!side) {
            ///Positions for when scoring on Goal side
            /// each tile is 24, so 72 -s outer wall
            /// heading is 0 facing positive x and starts increasing in the +x +y section of the field.
            ///  heading is in radians, can use Math.toRadians() to put in degrees
            startPose = new Pose2dWrapper(52, -52, Math.toRadians(135));
            firstLaunchPose = new Pose2dWrapper(12, -27, Math.toRadians(45));
            firstPickupPose = new Pose2dWrapper(12, -27, Math.toRadians(90));
            firstPickupFinishedPose = new Pose2dWrapper(12, -48, Math.toRadians(90));

            ///Experimental Auto-Code
            if (experimental) {

            }
        } else {
            ///Positions for when scoring on HPZ Side
            startPose = new Pose2dWrapper(-72, -26, Math.toRadians(0.1));
            firstLaunchPose = new Pose2dWrapper(-36, -29, Math.toRadians(45));
            firstPickupPose = new Pose2dWrapper(-36, -29, Math.toRadians(90));
            firstPickupFinishedPose = new Pose2dWrapper(-36, -52, Math.toRadians(90));
            secondLaunchPose = new Pose2dWrapper(-36, -52, Math.toRadians(45));
        }


        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose.toPose2d(), detailsLog,true);
        if (!drive.controlHub.isMacAddressValid()) {
            drive.controlHub.reportBadMacAddress(telemetry,hardwareMap);
        }
    /// START AUTO:
        telemetry.addData("waiting for start","");
        telemetry.update();
    waitForStart();
        telemetry.addData("started","");
        telemetry.update();
        if(

    isStopRequested())return;
        telemetry.addData("stop not requested","");
        telemetry.update();


    /// Auto code for when scoring Specimens
        if(!side)

    {
        telemetry.addData("goal side started","");
        telemetry.update();
        Actions.runBlocking(
                drive.actionBuilder(startPose.toPose2d())
                        .strafeToConstantHeading(firstLaunchPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move1","");
        telemetry.update();
        //wait for launch
        //launch
        Actions.runBlocking(
                drive.actionBuilder(firstLaunchPose.toPose2d())
                        .strafeToConstantHeading(firstPickupPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move2","");
        telemetry.update();
        Actions.runBlocking(
                drive.actionBuilder(firstPickupPose.toPose2d())
                        .strafeToConstantHeading(firstPickupFinishedPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move3","");
        telemetry.update();
        /*if (experimental) {

            telemetry.addData("goal side experimental started","");
            telemetry.update();
        }*/
    } else

    {
        telemetry.addData("HPZ side started","");
        telemetry.update();
        Actions.runBlocking(
                drive.actionBuilder(startPose.toPose2d())
                        .strafeToConstantHeading(firstLaunchPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move1","");
        telemetry.update();
        //wait for launch
        //launch
        Actions.runBlocking(
                drive.actionBuilder(firstLaunchPose.toPose2d())
                        .strafeToConstantHeading(firstPickupPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move2","");
        telemetry.update();
        Actions.runBlocking(
                drive.actionBuilder(firstPickupPose.toPose2d())
                        .strafeToConstantHeading(firstPickupFinishedPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move3","");
        telemetry.update();
        Actions.runBlocking(
                drive.actionBuilder(firstPickupFinishedPose.toPose2d())
                        .strafeToConstantHeading(secondLaunchPose.toPose2d().position)
                        .build()
        );
        telemetry.addData("move4","");
        telemetry.update();
        //wait for launch
        //launch

        /*if (experimental) {

        } else {
            /// Auto code for when scoring on HPZ side


        }*/
    }
}

    /**
     *
     *
     * @return an action to be run, parsable by RoadRunner
     */



}
