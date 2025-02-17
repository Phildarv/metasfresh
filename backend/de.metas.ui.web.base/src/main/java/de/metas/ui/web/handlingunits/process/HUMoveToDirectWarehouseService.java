/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2020 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package de.metas.ui.web.handlingunits.process;

import com.google.common.collect.ImmutableSet;
import de.metas.handlingunits.HuId;
import de.metas.handlingunits.IHandlingUnitsBL;
import de.metas.handlingunits.model.I_M_HU;
import de.metas.handlingunits.movement.api.IHUMovementBL;
import de.metas.handlingunits.movement.api.impl.HUMovementBuilder;
import de.metas.interfaces.I_M_Movement;
import de.metas.logging.LogManager;
import de.metas.ui.web.handlingunits.HUEditorView;
import de.metas.ui.web.view.event.ViewChangesCollector;
import de.metas.ui.web.window.model.DocumentCollection;
import de.metas.util.ILoggable;
import de.metas.util.Loggables;
import de.metas.util.Services;
import lombok.NonNull;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.model.PlainContextAware;
import org.adempiere.util.lang.IAutoCloseable;
import org.compiere.model.I_M_Warehouse;
import org.compiere.util.Env;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.util.Iterator;

/**
 * HU Editor service: Move selected HUs to direct warehouse (aka Materialentnahme)
 *
 * @author metas-dev <dev@metasfresh.com>
 */
public class HUMoveToDirectWarehouseService
{
	public static HUMoveToDirectWarehouseService newInstance()
	{
		return new HUMoveToDirectWarehouseService();
	}

	// services
	private static final transient Logger logger = LogManager.getLogger(HUMoveToDirectWarehouseService.class);
	private final transient IHUMovementBL huMovementBL = Services.get(IHUMovementBL.class);
	private DocumentCollection documentsCollection; // to be configured

	// parameters
	private Timestamp _movementDate = null;
	private String _description = null;
	private boolean _failOnFirstError = false;
	private boolean _failIfNoHUs = false; // default false for backward compatibility
	private ILoggable loggable = Loggables.nop();
	private HUEditorView huView;

	// state
	private transient I_M_Warehouse _targetWarehouse;

	private HUMoveToDirectWarehouseService()
	{
	}

	public void move(@NonNull final Iterator<I_M_HU> hus)
	{
		checkPreconditions();

		try (final IAutoCloseable ignored = ViewChangesCollector.currentOrNewThreadLocalCollector())
		{
			//
			// Move the HUs, one by one
			int countMoved = 0;
			while (hus.hasNext())
			{
				final I_M_HU hu = hus.next();
				generateMovement(hu);
				countMoved++;
			}

			// Stop here if nothing moved
			if (countMoved <= 0)
			{
				if (isFailIfNoHUs())
				{
					throw new AdempiereException("@NoSelection@");
				}
				return;
			}

			// Invalidate given view, if any
			if (huView != null)
			{
				huView.invalidateAll();
			}
		}
	}

	private void checkPreconditions()
	{
		getTargetWarehouse(); // will fail if direct warehouse is not configured or found
	}

	/**
	 * Generate a movement which will move given HU to {@link #getTargetWarehouse()}.
	 */
	private void generateMovement(@NonNull final I_M_HU hu)
	{
		final I_M_Warehouse targetWarehouse = getTargetWarehouse();

		try
		{
			//
			// Move the HU
			final I_M_Movement movement = new HUMovementBuilder()
					.setContextInitial(PlainContextAware.newWithThreadInheritedTrx())
					.setLocatorFrom(IHandlingUnitsBL.extractLocator(hu))
					.setWarehouseTo(targetWarehouse)
					.setMovementDate(getMovementDate())
					.setDescription(getDescription())
					.addHU(hu)
					.createMovement();
			if (movement == null)
			{
				throw new AdempiereException("No Movement created");
			}

			//
			// Notify listeners/handlers
			notifyHUMoved(hu);

			loggable.addLog("@Created@ @M_Movement_ID@: {}", movement.getDocumentNo());
		}
		catch (final Exception ex)
		{
			if (isFailOnFirstError())
			{
				throw AdempiereException.wrapIfNeeded(ex)
						.setParameter("HU", hu)
						.markAsUserValidationError();
			}

			final String errmsg = "Error on " + hu.getValue() + ": " + ex.getLocalizedMessage();
			loggable.addLog(errmsg);
			logger.warn(errmsg, ex);
		}
	}

	public HUMoveToDirectWarehouseService setMovementDate(final Timestamp movementDate)
	{
		_movementDate = movementDate;
		return this;
	}

	private Timestamp getMovementDate()
	{
		return _movementDate;
	}

	public HUMoveToDirectWarehouseService setDescription(final String description)
	{
		_description = description;
		return this;
	}

	private String getDescription()
	{
		return _description;
	}

	public HUMoveToDirectWarehouseService setLoggable(@NonNull final ILoggable loggable)
	{
		this.loggable = loggable;
		return this;
	}

	public HUMoveToDirectWarehouseService setFailOnFirstError(final boolean failOnFirstError)
	{
		_failOnFirstError = failOnFirstError;
		return this;
	}

	private boolean isFailOnFirstError()
	{
		return _failOnFirstError;
	}

	public HUMoveToDirectWarehouseService setFailIfNoHUs(final boolean failIfNoHUs)
	{
		_failIfNoHUs = failIfNoHUs;
		return this;
	}

	private boolean isFailIfNoHUs()
	{
		return _failIfNoHUs;
	}

	public HUMoveToDirectWarehouseService setDocumentsCollection(final DocumentCollection documentsCollection)
	{
		this.documentsCollection = documentsCollection;
		return this;
	}

	public HUMoveToDirectWarehouseService setHUView(final HUEditorView huView)
	{
		this.huView = huView;
		return this;
	}

	private void notifyHUMoved(final I_M_HU hu)
	{
		final HuId huId = HuId.ofRepoId(hu.getM_HU_ID());

		//
		// Invalidate all documents which are about this HU.
		if (documentsCollection != null)
		{
			try
			{
				documentsCollection.invalidateDocumentByRecordId(I_M_HU.Table_Name, huId.getRepoId());
			}
			catch (final Exception ex)
			{
				logger.warn("Failed invalidating documents for M_HU_ID={}. Ignored", huId, ex);
			}
		}

		//
		// Remove this HU from the view
		// Don't invalidate. We will do it at the end of all processing.
		//
		// NOTE/Later edit: we decided to not remove it anymore
		// because in some views it might make sense to keep it there.
		// The right way would be to check if after moving it, the HU is still elgible for view's filters.
		//
		// if (huView != null) { huView.removeHUIds(ImmutableSet.of(huId)); }
	}

	/**
	 * @return target warehouse where the HUs will be moved to.
	 */
	@NonNull
	private I_M_Warehouse getTargetWarehouse()
	{
		if (_targetWarehouse == null)
		{
			final boolean exceptionIfNull = true;
			_targetWarehouse = huMovementBL.getDirectMove_Warehouse(Env.getCtx(), exceptionIfNull);
		}
		return _targetWarehouse;
	}
}
