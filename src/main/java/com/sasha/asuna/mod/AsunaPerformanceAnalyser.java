/*
 * Copyright (c) Sasha Stevens (2017 - 2018)
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.asuna.mod;

import java.util.ArrayList;

/**
 * Created by Sasha on 11/08/2018 at 6:36 PM
 **/
public class AsunaPerformanceAnalyser {

    private ArrayList<Long> performanceTimesNormal;
    private ArrayList<Long> performanceTimesRender;

    public AsunaPerformanceAnalyser() {
        performanceTimesNormal = new ArrayList<>();
        performanceTimesRender = new ArrayList<>();
    }

    public void recordNewNormalTime(long totalTime) {
        performanceTimesNormal.add(totalTime);
    }

    public void recordNewRenderTime(long totalTime) {
        performanceTimesRender.add(totalTime);
    }

    public long getCurrentMsNormal() {
        return (long) (performanceTimesNormal.get(performanceTimesNormal.size() - 1) / 1e+6);
    }

    public double getAvgMsNormal() {
        int sum = 0;
        for (long i : performanceTimesNormal) {
            sum += i;
        }
        return (double) (sum / performanceTimesNormal.size()) / 1e+6;
    }

    public long getCurrentMsRender() {
        return (long) (performanceTimesRender.get(performanceTimesRender.size() - 1) / 1e+6);
    }

    public double getAvgMsRender() {
        int sum = 0;
        for (long i : performanceTimesRender) {
            sum += i;
        }
        return (double) (sum / performanceTimesRender.size()) / 1e+6;
    }

}
