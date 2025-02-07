// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.SignalLogger;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;


public class Robot extends TimedRobot {
  private static final int kMotorPort = 11;


  private final TalonFX m_motor;
  private boolean m_toggle = true;
  private double voltageOut = 2; // Volts
  private double maxSpeed = 2000; // RPM

  public Robot() {
    m_motor = new TalonFX(kMotorPort);

    var talonFXConfigurator = m_motor.getConfigurator();
    var limitConfigs = new CurrentLimitsConfigs();
    limitConfigs.StatorCurrentLimit = 60;
    limitConfigs.StatorCurrentLimitEnable = true;
    talonFXConfigurator.apply(limitConfigs);
    
    m_motor.setNeutralMode(NeutralModeValue.Coast);

    SignalLogger.setPath("/media/sda11/");
    

  }

  /*
   * The RobotPeriodic function is called every control packet no matter the
   * robot mode.
   */
  @Override
  public void robotPeriodic() {}
  @Override
  public void teleopInit() {
    m_toggle = true;
    SignalLogger.start();
    SignalLogger.enableAutoLogging(true);

  }

  /** The teleop periodic function is called every control packet in teleop. */
  @Override
  public void teleopPeriodic() {

    voltageOut = Shuffleboard.getTab("Tuning").add("Voltage Out", voltageOut).getEntry().getDouble(voltageOut);
    maxSpeed = Shuffleboard.getTab("Tuning").add("Max Speed", maxSpeed).getEntry().getDouble(maxSpeed);
    if (m_toggle) {
      m_motor.setControl(new VoltageOut(voltageOut));
    } else {
      m_motor.stopMotor();
    }
    
    if (Math.abs(m_motor.getVelocity().getValueAsDouble()) > maxSpeed/60) {
      m_toggle = false;
      SignalLogger.stop();
    }

  }
  private void log() {
    SignalLogger.writeDouble("position", m_motor.getPosition().getValueAsDouble());
    SignalLogger.writeDouble("veloicty", m_motor.getVelocity().getValueAsDouble());
    SignalLogger.writeDouble("acceleration", m_motor.getAcceleration().getValueAsDouble());
    SignalLogger.writeDouble("voltage", m_motor.getMotorVoltage().getValueAsDouble());

  }

  // @Override
  // public void end() {
  //   // Explicitly stop logging
  //   // If the user does not call stop(), then it's possible to lose the last few seconds of data
  //   SignalLogger.stop();
  // }
}
