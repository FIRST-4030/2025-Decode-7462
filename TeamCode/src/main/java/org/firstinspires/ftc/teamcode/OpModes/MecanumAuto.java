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

    public static boolean logHPZSide = false;
    public static boolean logGoalSide = false;
    public static boolean logDetails = false;
    public double waitForLaunch;
    public Pose2dWrapper startPose;
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
            telemetry.addData("Compiled on:", BuildConfig.COMPILATION_DATE);
            telemetry.addData("-----Initialization-----", "");
            telemetry.addLine();
            telemetry.addData("Auto Mode: ", side ? "HPZ side Scoring" : "Goal side Scoring");
            telemetry.addData("Experimental Mode? ", experimental ? "Enabled" : "Disabled");
            telemetry.addData("Press X to finalize values", inputComplete);
            telemetry.update();

        }
        if (!side) {
            ///Positions for when scoring on Goal side
            /// each tile is 24, so 72 -s outer wall
            /// heading is 0 facing positive x and starts increasing in the +x +y section of the field.
            ///  heading is in radians, can use Math.toRadians() to put in degrees
            startPose = new Pose2dWrapper(52, -52, 4.7123);
            ///Experimental Auto-Code
            if (experimental) {

            }
        } else {
            ///Positions for when scoring on HPZ Side
            startPose = new Pose2dWrapper(-72, -26, 4.7123);
        }




    /// START AUTO:
    waitForStart();
        if(

    isStopRequested())return;


    /// Auto code for when scoring Specimens
        if(!side)

    {
        if (!experimental) {

        }
    } else

    {
        if (experimental) {

        } else {
            /// Auto code for when scoring on HPZ side


        }
    }
}

    /**
     *
     *
     * @return an action to be run, parsable by RoadRunner
     */



}
