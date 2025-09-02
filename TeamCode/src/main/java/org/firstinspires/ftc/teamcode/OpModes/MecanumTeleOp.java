package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.LogFile;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.gamepad.InputAutoMapper;
import org.firstinspires.ftc.teamcode.gamepad.InputHandler;
import org.firstinspires.ftc.teamcode.math.maths.vectors.Vector3d;

@Config
@TeleOp
public class MecanumTeleOp extends OpMode {
    public static boolean logDetails = false;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime headingTimer = new ElapsedTime();
    double deltaTime;
    double previousTime;
    IMU imu;
    InputHandler inputHandler;
    LogFile detailsLog;
    MecanumDrive drive;
    Orientation or;
    Servo cvServo;
    Vector3d mecanumController;
    boolean precisionDrive = false, resetIMU = false;
    boolean resetHeading = false;
    boolean slowMode = false;
    double driveCoefficient = 1;
    double globalIMUHeading, headingError = 0, powerCoefficient = 1;

    //Create a hash map with keys: dpad buttons, and values: ints based on the corresponding joystick value of the dpad if is pressed and 0 if it is not
    //Ex. dpad Up = 1, dpad Down = -1
    //I chose to use a hashmap for human readability, even if it adds more lines of code, unsure if this was the correct choice but hey, I made it
    double[] dpadPowerArray = new double[4];

    @Override
    public void init() {
        telemetry.addData("Compiled on:", BuildConfig.COMPILATION_DATE);
        telemetry.addLine("Waiting for start");
        telemetry.update();

        inputHandler = InputAutoMapper.normal.autoMap(this);

        imu = hardwareMap.get(IMU.class, "imu");

        if (logDetails) { detailsLog = new LogFile(LogFile.FileType.Details, "details", "csv"); }

        drive = new MecanumDrive(hardwareMap, new Pose2d(new Vector2d(0, 0), 0), detailsLog, logDetails);
        if (!drive.controlHub.isMacAddressValid()) {
            drive.controlHub.reportBadMacAddress(telemetry, hardwareMap);
            telemetry.update();
        }

        cvServo = hardwareMap.get(Servo.class, "servo");

        mecanumController = new Vector3d();

        imu.resetYaw();
        or = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS);
        globalIMUHeading = or.thirdAngle;
    }

    @Override
    public void loop() {
        if(slowMode) {
            driveCoefficient = 0.65;
        } else {
            driveCoefficient = 1;
        }
        handleInput();

        deltaTime = timer.milliseconds() - previousTime;
        previousTime += deltaTime;

        or = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS);

        headingError = or.thirdAngle - globalIMUHeading;
        if(headingError > Math.PI) headingError -= 2*Math.PI;
        else if(headingError < -Math.PI) headingError += 2*Math.PI;

//        Pose2d pose = drive.localizer.getPose();
//        telemetry.addData("x", pose.position.x);
//        telemetry.addData("y", pose.position.y);
        telemetry.addData("error: ", headingError);

        resetIMU = drive.update(mecanumController, dpadPowerArray, headingError, resetIMU, powerCoefficient, precisionDrive);

        telemetry.update();
    }

    public void handleInput () {
        inputHandler.loop();

        mecanumController = new Vector3d((gamepad1.right_stick_x * driveCoefficient), (gamepad1.right_stick_y * driveCoefficient), (gamepad1.left_stick_x * driveCoefficient));

        //Reset Field-Centric drive by pressing B
        if(inputHandler.up("D1:B")){
            resetIMU = true;
        }

        if(gamepad1.left_stick_x != 0){
            resetHeading = true;
            headingTimer.reset();
        }

        if(resetHeading){
            if(headingTimer.milliseconds() > 250){
                globalIMUHeading = or.thirdAngle;
                resetHeading = false;
            }
        }

        slowMode = inputHandler.active("D1:RT");
    }
}
