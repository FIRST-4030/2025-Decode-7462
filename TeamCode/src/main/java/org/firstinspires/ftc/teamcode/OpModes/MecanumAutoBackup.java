package org.firstinspires.ftc.teamcode.OpModes;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.gamepad.InputAutoMapper;
import org.firstinspires.ftc.teamcode.gamepad.InputHandler;
import org.firstinspires.ftc.teamcode.opencv.DrawRectanglePipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Config
@Autonomous
public class MecanumAutoBackup extends LinearOpMode {
    /*
    * This autonomous mode uses OpenCV to sense the position of a specimen and
    * moves a servo based upon which of 3 rectangles it finds the object
     */
    public static int c270_width  = 320;
    public static int c270_height = 240;
    public static int c1080_width  = 640;
    public static int c1080_height = 480;
    public static double pos1 = 0.0;
    public static double pos2 = 0.5;
    public static double pos3 = 1.0;

    public String[] cameraNames = {"WebcamC270", "Webcam1080"};

    InputHandler inputHandler;
    OpenCvWebcam webcam;
    DrawRectanglePipeline pipeline;
    String camera = null;
    Servo cvServo;
    boolean inputComplete= false;
    int camera_width, camera_height;
    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        inputHandler = InputAutoMapper.normal.autoMap(this);

        while (!inputComplete) {
            inputHandler.loop();
            if (inputHandler.up("D1:X")) {
                inputComplete = true;
            }
            if (inputHandler.up("D1:Y")) {
                camera = cameraNames[0];
                camera_width = c270_width;
                camera_height = c270_height;
            }
            if (inputHandler.up("D1:A")) {
                camera = cameraNames[1];
                camera_width = c1080_width;
                camera_height = c1080_height;
            }

            telemetry.addData("Compiled on:", BuildConfig.COMPILATION_DATE);
            telemetry.addLine();
            telemetry.addLine("Button A: Webcam 1080");
            telemetry.addLine("Button Y: Webcam C270");
            telemetry.addData("Press X to finalize values", inputComplete);
            telemetry.update();
        }
        sleep(500);

        telemetry.addData(camera, " will be used");
        telemetry.addLine("Waiting for start");
        telemetry.update();

        pipeline = new DrawRectanglePipeline(telemetry, camera);

        cvServo = hardwareMap.get(Servo.class, "servo");

        int cameraMonitorViewId = hardwareMap.appContext
                .getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        webcam = OpenCvCameraFactory
                .getInstance()
                .createWebcam(hardwareMap.get(org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName.class, camera),
                cameraMonitorViewId);

        webcam.setPipeline(pipeline);

        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(webcam, 30); // 30 FPS stream

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(camera_width, camera_height, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addData("Camera Error", errorCode);
                telemetry.update();
            }
        });

        waitForStart();

        webcam.stopStreaming();

        if (isStopRequested()) return;

        switch (pipeline.selectedRect) {
            case 1:
                cvServo.setPosition(pos3);   // Force the servo to move first
                sleep(500);
                telemetry.addData("Set Servo to: ", String.format("%.2f",pos1));
                cvServo.setPosition(pos1);
                break;
            case 2:
                cvServo.setPosition(pos1);   // Force the servo to move first
                sleep(500);
                telemetry.addData("Set Servo to: ", String.format("%.2f",pos2));
                cvServo.setPosition(pos2);
                break;
            case 3:
                cvServo.setPosition(pos2);   // Force the servo to move first
                sleep(500);
                telemetry.addData("Set Servo to: ", String.format("%.2f",pos3));
                cvServo.setPosition(pos3);
                break;
        }

        telemetry.addData("Selected Rectangle: ",pipeline.selectedRect);
        telemetry.update();
        sleep(1000);
    }
}
