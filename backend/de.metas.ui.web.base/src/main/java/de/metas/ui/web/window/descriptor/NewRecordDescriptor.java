package de.metas.ui.web.window.descriptor;

import de.metas.ui.web.window.datatypes.WindowId;
import de.metas.ui.web.window.model.Document;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2017 metas GmbH
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

/**
 * Describes which window to be used to capture the fields for quickly creating a record for a given BPartner.
 * <p>
 * task https://github.com/metasfresh/metasfresh/issues/1090
 */
@Value(staticConstructor = "of")
public class NewRecordDescriptor
{
	public interface NewRecordProcessor
	{
		int processNewRecordDocument(Document document);
	}

	@NonNull String tableName;
	@NonNull WindowId newRecordWindowId;
	@NonNull NewRecordProcessor processor;
}
