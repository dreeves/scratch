package takns;

import lombok.*;

import takns.main.*;
import static takns.main.*;

public class ai {

static class PathFinder {
	int[] estimatedCosts;
	int[] costs;
	int[] travelCosts;
	int xStart;
	int yStart;
	int xEnd;
	int yEnd;
	private int xTile;
	private int yTile;

	int[] path = new int[64 * 64];
	int pathP = 0;

	boolean isPathing = false;
	int calls = 0;
	int[] estimates = new int[64 * 64];
	int ep = 0;
	int closestValue;
	int xClosest;
	int yClosest;

	void startFindingPath(int[] travelCosts, int xStart, int yStart, int xEnd, int yEnd) {
		this.travelCosts = travelCosts;
		estimatedCosts = new int[64 * 64];
		costs = new int[64 * 64];
		ep = 0;

		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;

		xTile = xStart;
		yTile = yStart;

		costs[xTile + yTile * 64] = 1;
		isPathing = true;
		calls = 0;

		closestValue = -1;
	}
	void continueFindingPath(int maxVisits) {
		long startTime = System.nanoTime();
		calls++;
		int visits = 0;
		do {
			visits++;
			if (visits == maxVisits) return;

			int p = xTile + yTile * 64;
			int baseCost = costs[p];
			estimatedCosts[p] = -1;

			for (int x = xTile - 1; x <= xTile + 1; x++) {
				for (int y = yTile - 1; y <= yTile + 1; y++) {
					if (x == xTile && y == yTile) continue;
					if (x < 0 || y < 0 || x >= 64 || y >= 64) continue;
					p = x + y * 64;
					if (estimatedCosts[p] < 0) continue;
					if (travelCosts[p] == 0) continue;

					int dist = (x == xTile || y == yTile) ? 10 : 14;
					int costSoFar = baseCost + travelCosts[p] * dist;
					int xd = (xEnd - x);
					int yd = (yEnd - y);
					if (xd < 0) xd = -xd;
					if (yd < 0) yd = -yd;
					int remainingCost = (xd + yd) * 100;
					int estimatedCost = costSoFar + remainingCost;
					if (estimatedCosts[p] > 0) {
						if (estimatedCosts[p] < estimatedCost) continue;
					} else estimates[ep++] = p;

					if (closestValue == -1 || remainingCost < closestValue) {
						closestValue = remainingCost;
						xClosest = x;
						yClosest = y;
					}

					costs[p] = costSoFar;
					estimatedCosts[p] = estimatedCost;
				}
			}

			xTile = -1;
			int lowestCost = 9999999;
			int epi = -1;
			for (int i = 0; i < ep; i++) {
				p = estimates[i];
				if (estimatedCosts[p] > 0 && estimatedCosts[p] < lowestCost) {
					epi = i;
					lowestCost = estimatedCosts[p];
					xTile = p & 63;
					yTile = p / 64;
				}
			}

			if (epi >= 0) estimates[epi] = estimates[--ep];
		}
		while ((xTile != xEnd || yTile != yEnd) && (xTile >= 0));
		isPathing = false;

		if (xTile == -1) {
			// Failed to find path
			if (closestValue != -1) {
				xTile = xClosest;
				yTile = yClosest;
			}
		} // else did find path

		if (xTile != -1) {
			startTime = System.nanoTime();
			pathP = 0;
			path[pathP++] = xTile + yTile * 64;
			while (xTile != xStart || yTile != yStart) {
				int lowest = -1;
				int xt = -1;
				int yt = -1;
				for (int xx = -1; xx <= 1; xx++) {
					for (int yy = -1; yy <= 1; yy++) {
						if (xx == 0 && yy == 0) continue;
						int x = xTile + xx;
						int y = yTile + yy;
						if (x < 0 || y < 0 || x >= 64 || y >= 64) continue;
						if (costs[x + y * 64] <= 0) continue;

						if (lowest == -1 || costs[x + y * 64] < lowest) {
							lowest = costs[x + y * 64];
							xt = x;
							yt = y;
						}
					}
				}
				xTile = xt;
				yTile = yt;
				path[pathP++] = xTile + yTile * 64;
			}
		}
	}
}

}